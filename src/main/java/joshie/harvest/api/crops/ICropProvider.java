package joshie.harvest.api.crops;

import net.minecraft.item.ItemStack;

/** Items that implement this interface can supply a crop item **/
@Deprecated //TODO: Remove in 0.7+
public interface ICropProvider {
    /** Returns the crop that this item provides
     *  
     *  @param  stack   the item
     *  @return         the crop that it is providing **/
    Crop getCrop(ItemStack stack);
}