package joshie.harvest.cooking.render;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import joshie.harvest.api.cooking.Utensil;
import joshie.harvest.cooking.recipe.MealImpl;
import joshie.harvest.core.base.FMLDefinition;
import joshie.harvest.core.base.ItemHFFML;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;

public class MealDefinition extends FMLDefinition<MealImpl> {
    public static final int MAX_UTENSILS_DISPLAY = 5;
    private TIntObjectMap<ModelResourceLocation> burnt = new TIntObjectHashMap<>();

    public MealDefinition(ItemHFFML item, String name, FMLControlledNamespacedRegistry<MealImpl> registry) {
        super(item, name, registry);
    }

    public void registerBurnt(int damage, ModelResourceLocation resource) {
        burnt.put(damage, resource);
    }

    public int getMetaFromStack(ItemStack stack) {
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < Utensil.UTENSILS.length) {
            return stack.getItemDamage();
        }

        return 0;
    }

    @Override
    public void registerEverything() {
        super.registerEverything();
        //Register the burnt meals
        for (int i = 0; i < Utensil.UTENSILS.length; i++) {
            Utensil utensil = Utensil.getUtensilFromIndex(i);
            ModelResourceLocation model = utensil.getModelForMeal();
            ModelBakery.registerItemVariants(item, model);
            registerBurnt(utensil.getIndex(), model);
        }
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        if (stack.hasTagCompound()) {
            return super.getModelLocation(stack);
        }

        return burnt.get(getMetaFromStack(stack));
    }
}
