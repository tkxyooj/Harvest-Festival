package joshie.harvest.knowledge.item;

import joshie.harvest.HarvestFestival;
import joshie.harvest.core.base.item.ItemHFEnum;
import joshie.harvest.core.util.interfaces.ICreativeSorted;
import joshie.harvest.knowledge.HFKnowledge;
import joshie.harvest.knowledge.item.ItemBook.Book;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Locale;

import static joshie.harvest.core.handlers.GuiHandler.CALENDAR_GUI;
import static joshie.harvest.core.handlers.GuiHandler.STATS_BOOK;

public class ItemBook extends ItemHFEnum<ItemBook, Book> implements ICreativeSorted {
    public enum Book implements IStringSerializable {
        STATISTICS(STATS_BOOK), CALENDAR(CALENDAR_GUI);

        private final int guiID;

        Book(int guiID) {
            this.guiID = guiID;
        }

        public int getGuiID() {
            return guiID;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    public ItemBook() {
        super(Book.class);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!player.isSneaking()) {
            player.openGui(HarvestFestival.instance, getEnumFromStack(stack).getGuiID(), world, 0, 0, 0);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        boolean replaceable = state.getBlock().isReplaceable(world, pos);

        if (getEnumFromStack(stack) == Book.CALENDAR && facing != EnumFacing.UP && facing != EnumFacing.DOWN && (state.getMaterial().isSolid() || replaceable)) {
            pos = pos.offset(facing);

            if (player.canPlayerEdit(pos, facing, stack) && HFKnowledge.CALENDAR.canPlaceBlockAt(world, pos)) {
                if (world.isRemote) {
                    return EnumActionResult.SUCCESS;
                } else {
                    world.setBlockState(pos, HFKnowledge.CALENDAR.getDefaultState().withProperty(BlockWallSign.FACING, facing), 11);

                    --stack.stackSize;
                    return EnumActionResult.SUCCESS;
                }
            } else {
                return EnumActionResult.FAIL;
            }
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public int getSortValue(ItemStack stack) {
        return 1000;
    }
}