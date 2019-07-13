package com.latmod.mods.tesslocator.item;

import com.latmod.mods.tesslocator.block.BlockTesslocator;
import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.block.part.EnumPartType;
import com.latmod.mods.tesslocator.block.part.TesslocatorPart;
import com.latmod.mods.tesslocator.data.TessNet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemTesslocator extends Item
{
	public final EnumPartType type;
	public boolean disabled;

	public ItemTesslocator(EnumPartType t)
	{
		type = t;
		disabled = false;
	}

	public ItemTesslocator setDisabled()
	{
		disabled = true;
		return this;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (disabled)
		{
			return EnumActionResult.SUCCESS;
		}

		if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos))
		{
			pos = pos.offset(facing);
		}

		ItemStack stack = player.getHeldItem(hand);
		TileEntity tileEntity = world.getTileEntity(pos);

		if (player.canPlayerEdit(pos, facing, stack) && (tileEntity instanceof TileTesslocator || world.mayPlace(world.getBlockState(pos).getBlock(), pos, false, facing, null)))
		{
			if (!(tileEntity instanceof TileTesslocator))
			{
				world.setBlockState(pos, BlockTesslocator.INSTANCE.getDefaultState(), 11);
				tileEntity = world.getTileEntity(pos);
			}

			if (tileEntity instanceof TileTesslocator)
			{
				TileTesslocator tile = (TileTesslocator) tileEntity;
				EnumFacing opposite = facing.getOpposite();

				if (tile.parts[opposite.getIndex()] != null)
				{
					return EnumActionResult.FAIL;
				}

				TesslocatorPart part = type.provider.createPart(tile, opposite);
				tile.parts[opposite.getIndex()] = part;
				part.onPlaced(player, stack);
				tileEntity.markDirty();

				if (!world.isRemote && TessNet.SERVER != null)
				{
					TessNet.SERVER.markDirty();
				}
			}

			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 11);

			if (player instanceof EntityPlayerMP)
			{
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
			}

			SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
			world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

			if (!player.capabilities.isCreativeMode)
			{
				stack.shrink(1);
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		if (disabled)
		{
			tooltip.add(TextFormatting.RED + "Currently disabled, because it's not finished!");
		}

		if (type.isAdvanced)
		{
			int col = stack.hasTagCompound() ? (stack.getTagCompound().getByte("colors") & 0xFF) : 0;
			tooltip.add("Color A: " + I18n.format("item.fireworksCharge." + EnumDyeColor.byMetadata(col & 0xF).getTranslationKey()));
			tooltip.add("Color B: " + I18n.format("item.fireworksCharge." + EnumDyeColor.byMetadata((col >> 4) & 0xF).getTranslationKey()));
			tooltip.add("");
			tooltip.add(TextFormatting.DARK_GRAY + "Change colors by holding dye in each hand and right-clicking on block");
		}
	}
}