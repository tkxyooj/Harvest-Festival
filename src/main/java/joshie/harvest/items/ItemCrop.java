package joshie.harvest.items;

import joshie.harvest.api.core.ICreativeSorted;
import joshie.harvest.api.core.IShippable;
import joshie.harvest.api.crops.ICrop;
import joshie.harvest.api.crops.ICropProvider;
import joshie.harvest.core.HFTab;
import joshie.harvest.core.lib.CreativeSort;
import joshie.harvest.core.util.base.ItemHFBaseMeta;
import joshie.harvest.crops.HFCrops;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemCrop extends ItemHFBaseMeta implements IShippable, ICropProvider, ICreativeSorted {
    private ICrop crop;

    public ItemCrop(ICrop crop) {
        setCreativeTab(HFTab.FARMING);
        this.crop = crop;
    }

    @Override
    public int getMetaCount() {
        return 1;
    }

    @Override
    public int getSortValue(ItemStack stack) {
        return CreativeSort.CROPS;
    }

    @Override
    public long getSellValue(ItemStack stack) {
        if (crop == HFCrops.grass) return 0;
        else {
            return crop.getSellValue();
        }
    }

    @Override
    public ICrop getCrop(ItemStack stack) {
        return crop;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return crop.getLocalizedName(true);
    }

    @Override
    public String getName(ItemStack stack) {
        return crop.getUnlocalizedName();
    }

    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{HFTab.FARMING};
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (crop.getCropStack() != null) list.add(crop.getCropStack());
    }
}