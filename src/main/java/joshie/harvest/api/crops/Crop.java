package joshie.harvest.api.crops;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.animals.AnimalFoodType;
import joshie.harvest.api.calendar.Season;
import joshie.harvest.api.cooking.Ingredient;
import joshie.harvest.api.core.IShippable;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;
import org.apache.commons.lang3.StringUtils;

public class Crop extends IForgeRegistryEntry.Impl<Crop> implements IShippable {
    public static final IForgeRegistry<Crop> REGISTRY = new RegistryBuilder<Crop>().setName(new ResourceLocation("harvestfestival", "crops")).setType(Crop.class).setIDRange(0, 32000).create();
    public static final GrowthHandler SEASONAL = new GrowthHandler() {};
    public static final DropHandler DROPS = new DropHandler();
    public static final Crop NULL_CROP = new Crop();

    //CropData
    private IStateHandler stateHandler;
    private GrowthHandler growthHandler;
    private DropHandler dropHandler;
    private AnimalFoodType foodType;
    private EnumPlantType type;
    private Ingredient ingredient;
    private Block growsToSide;
    private boolean needsWatering;
    private boolean alternativeName;
    private boolean requiresSickle;
    //TODO: Remove in 0.7+
    private int hunger;
    //TODO: Remove in 0.7+
    private float saturation;
    private ItemStack item;
    private Season[] seasons;
    private long cost;
    private long sell;
    private int stages;
    private int regrow;
    private int year;
    private int bagColor;
    private int witheredColor;
    private int doubleStage;
    private int minCut;
    private boolean skipRender;

    private Crop() {
        this(new ResourceLocation("harvestfestival", "null_crop"));
    }

    public Crop(ResourceLocation key) {
        this.seasons = new Season[] { Season.SPRING };
        this.stages = 3;
        this.foodType = AnimalFoodType.VEGETABLE;
        this.bagColor = 0xFFFFFF;
        this.witheredColor = 0xA64DFF;
        this.stateHandler = new StateHandlerDefault(this);
        this.growthHandler = SEASONAL;
        this.needsWatering = true;
        this.doubleStage = Integer.MAX_VALUE;
        this.type = EnumPlantType.Crop;

        this.setRegistryName(key);
        REGISTRY.register(this);
    }

    @Deprecated
    public Crop(ResourceLocation key, long cost, long sell, int stages, int color, Season... seasons) {
        this.seasons = seasons;
        this.cost = cost;
        this.sell = sell;
        this.stages = stages;
        this.regrow = 0;
        this.year = 0;
        this.alternativeName = false;
        this.foodType = AnimalFoodType.VEGETABLE;
        this.bagColor = color;
        this.witheredColor = 0xA64DFF;
        this.stateHandler = new StateHandlerDefault(this);
        this.growthHandler = SEASONAL;
        this.needsWatering = true;
        this.doubleStage = Integer.MAX_VALUE;
        this.dropHandler = null;
        this.growsToSide = null;
        this.type = EnumPlantType.Crop;
        this.skipRender = false;
        this.setRegistryName(key);
        REGISTRY.register(this);
    }

    /** Set how much this tree costs to buy and sell
     * @param cost the cost
     * @param sell the sell value**/
    public Crop setGoldValues(long cost, long sell) {
        this.cost = cost;
        this.sell = sell;
        return this;
    }

    /** Set the colour of the seeds
     *  @param color the color */
    public Crop setSeedColours(int color) {
        this.bagColor = color;
        return this;
    }

    /** Set the seasons this tree grows in **/
    public Crop setSeasons(Season... seasons) {
        this.seasons = seasons;
        return this;
    }

    /** Set the growth lengths for this tree
     * @param stages the number of stages **/
    public Crop setStages(int stages) {
        this.stages = stages;
        return this;
    }

    /**
     * Set the year this crop is unlocked for purchased
     **/
    public Crop setYearUnlocked(int year) {
        this.year = year;
        return this;
    }

    /**
     * Set the stage at which crops regrow to
     **/
    public Crop setRegrowStage(int regrow) {
        this.regrow = regrow;
        return this;
    }

    /**
     * If the crop/seeds should have different names set this to true
     **/
    public Crop setHasAlternativeName() {
        this.alternativeName = true;
        return this;
    }

    /** Set the item for this crop
     *  @param item     the item to set this as**/
    public Crop setItem(ItemStack item) {
        this.item = item;
        if (this.item != null && this.item.getItem() instanceof ItemFood) {
            ItemFood food = (ItemFood)this.item.getItem();
            setIngredient(food.getHealAmount(item), food.getSaturationModifier(item));
        }

        return this;
    }

    /** Overloaded mehtod for setItem
     *  @param item the item **/
    public Crop setItem(Item item) {
        setItem(new ItemStack(item));
        return this;
    }

    /**
     * Set the state handler for this crop
     **/
    public Crop setStateHandler(IStateHandler handler) {
        this.stateHandler = handler;
        return this;
    }

    /**
     * Make this crop require a sickle to be harvested
     * Sets the minimum cut
     * Used when a crop requires the sickle,
     *  The crop will only be destroyable/harvestable
     *  if it's below this stage
     */
    public Crop setRequiresSickle(int minCut) {
        this.requiresSickle = true;
        this.minCut = minCut;
        return this;
    }

    /**
     * Set what this plant counts as for feeding animals
     * @param foodType the type of food
     **/
    public Crop setAnimalFoodType(AnimalFoodType foodType) {
        this.foodType = foodType;
        return this;
    }

    /**
     * Set the plant type
     * @param plantType     the plant type of this crop
     **/
    public Crop setPlantType(EnumPlantType plantType) {
        this.type = plantType;
        return this;
    }

    //TODO: Remove in 0.7+
    @Deprecated
    public Crop setFoodStats(int hunger, float saturation) {
        this.hunger = hunger;
        this.saturation = saturation;
        return setIngredient(hunger, saturation);
    }

    /**
     * Set the ingredient this crop counts as
     **/
    @Deprecated
    public Crop setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    /**
     * Set the ingredient stats for this crop
     **/
    public Crop setIngredient(int hunger, float saturation) {
        String name = getRegistryName().getResourcePath();
        if (Ingredient.INGREDIENTS.containsKey(name)) {
            this.ingredient = Ingredient.INGREDIENTS.get(name);
        } else this.ingredient = new Ingredient(name, hunger, saturation);

        return this;
    }

    /**
     * Make this crop need zero water to grow
     **/
    public Crop setNoWaterRequirements() {
        this.needsWatering = false;
        return this;
    }

    /**
     * Set the growth handler, to determine when this plant can grow/where
     * @param handler   the growth handler
     **/
    public Crop setGrowthHandler(GrowthHandler handler) {
        this.growthHandler = handler;
        return this;
    }

    /**
     * Set the stage at which this plant becomes two tall
     * @param doubleStage   the stage this plant becomes double
     **/
    public Crop setBecomesDouble(int doubleStage) {
        this.doubleStage = doubleStage;
        return this;
    }

    /**
     * Set the drop handler for this crop
     * @param handler   the drop handler
     **/
    public Crop setDropHandler(DropHandler handler) {
        this.dropHandler = handler;
        return this;
    }

    /**
     * Set the block this grows on the side of it
     * @param block the block to grow
     **/
    public Crop setGrowsToSide(Block block) {
        this.growsToSide = block;
        return this;
    }

    /** Set the colour for this crop
     *  to render as when withered */

    public Crop setWitheredColor(int color) {
        this.witheredColor = color;
        return this;
    }

    /**
     * This crop doesn't need to load it's renders,
     * It's blocks will be tinted as well
     **/
    public Crop setSkipRender() {
        this.skipRender = true;
        return this;
    }

    /**
     * This is the season this crop survives in
     *
     * @return the season that is valid for this crop
     */
    public Season[] getSeasons() {
        return seasons;
    }

    /**
     * This is how much the seed costs in the store.
     * If the seed isn't purchasable return 0
     *
     * @return the cost in gold
     */
    public long getSeedCost() {
        return cost;
    }

    /**
     * This is how much this crop well sell for at level 1.
     * If this crop cannot be sold return 0
     *
     * @param stack the crop
     * @return the sell value in gold
     */
    public long getSellValue(ItemStack stack) {
        return sell;
    }

    /**
     * Return how many stages this crop has.
     * A return value of 0, means the crop is instantly grown.
     *
     * @return the stage
     */
    public int getStages() {
        return stages;
    }

    /**
     * The year in which this crop can be purchased
     **/
    public int getPurchaseYear() {
        return year;
    }

    /**
     * Return the stage that the plant returns to when it's harvested.
     * A return value of 0, means the crop is destroyed.
     *
     * @return the stage
     */
    public int getRegrowStage() {
        return regrow;
    }

    /** Used when a crop requires the sickle,
     *  The crop will only be destroyable/harvestable
     *  if it's below this stage */
    public int getMinimumCut() {
        return minCut;
    }

    /**
     * Return true if this crop required a sickle
     * **/
    public boolean requiresSickle() {
        return requiresSickle;
    }

    /**
     * Return true if this crop requires water
     * **/
    public boolean requiresWater() {
        return needsWatering;
    }

    /**
     * Return true if this crop is turning double crop at this stage
     * @param stage the stage of the crop
     * **/
    public boolean isTurningToDouble(int stage) {
        return stage == doubleStage;
    }

    /**
     * Return true if this crop is a double crop at this stage
     * @param stage the stage of the crop
     * **/
    public boolean isCurrentlyDouble(int stage) {
        return stage >= doubleStage;
    }

    /**
     * Return true if this crop grows to the side like pumpkins
     * **/
    public Block growsToSide() {
        return growsToSide;
    }

    /**
     * Return the growth handler for this crop
     * **/
    public GrowthHandler getGrowthHandler() {
        return growthHandler;
    }

    /**
     * This is called when bringing up the list of crops for sale
     *
     * @return whether this item can be purchased in this shop or not
     */
    public boolean canPurchase() {
        return getSeedCost() > 0;
    }

    /**
     * Return the colour of the seed bag
     **/
    public int getColor() {
        return bagColor;
    }

    /**
     * Return the colour modifier when this crop is withered
     * **/
    public int getWitheredColor() {
        return witheredColor;
    }

    /**
     * The type of animal food this is
     **/
    public AnimalFoodType getFoodType() {
        return foodType;
    }

    /**
     * The ingredient this crop represents
     **/
    public Ingredient getIngredient() {
        return ingredient;
    }

    //TODO: Remove in 0.7+
    @Deprecated
    public int getHunger() {
        return hunger;
    }

    //TODO: Remove in 0.7+
    @Deprecated
    public float getSaturation() {
        return saturation;
    }

    /**
     * This crop as a seed stack
     **/
    public ItemStack getSeedStack(int amount) {
        return HFApi.crops.getSeedStack(this, amount);
    }

    /**
     *  This crop as a stack
     **/
    public ItemStack getCropStack(int amount) {
        if (item != null) {
            ItemStack copy = item.copy();
            copy.stackSize = amount;
            return item.copy();
        } else return new ItemStack(Items.CARROT);
    }

    /**
     * What this crop drops
     **/
    public DropHandler getDropHandler() {
        return dropHandler == null ? DROPS : dropHandler;
    }

    /**
     * The state handler for crop
     **/
    public IStateHandler getStateHandler() {
        return stateHandler;
    }

    /**
     * If the stack is this crop
     **/
    public boolean matches(ItemStack stack) {
        return HFApi.crops.getCropFromStack(stack) == this;
    }

    /**
     * The planty type of this crop
     **/
    public EnumPlantType getPlantType() {
        return type;
    }

    /** Whether to skip the render loading of this crop **/
    public boolean skipLoadingRender() {
        return skipRender;
    }

    /**
     * Gets the localized crop name for this crop
     *
     * @param isItem the item
     * @return crop name
     */
    @SuppressWarnings("deprecation")
    public String getLocalizedName(boolean isItem) {
        String suffix = alternativeName ? ((isItem) ? ".item" : ".block") : "";
        return I18n.translateToLocal((getRegistryName().getResourceDomain() + ".crop." + StringUtils.replace(getRegistryName().getResourcePath(), "_", ".") + suffix));
    }

    /**
     * Gets the localized seed name for this crop
     *
     * @return seed name
     */
    @SuppressWarnings("deprecation")
    public String getSeedsName() {
        String suffix = alternativeName ? ".block" : "";
        String name = I18n.translateToLocalFormatted((getRegistryName().getResourceDomain() + ".crop." + StringUtils.replace(getRegistryName().getResourcePath(), "_", ".") + suffix));
        String seeds = I18n.translateToLocal("harvestfestival.crop.seeds");
        String format = I18n.translateToLocal("harvestfestival.crop.seeds.format");
        return String.format(format, name, seeds);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Crop && getRegistryName().equals(((Crop) o).getRegistryName());
    }

    @Override
    public int hashCode() {
        return getRegistryName().hashCode();
    }
}