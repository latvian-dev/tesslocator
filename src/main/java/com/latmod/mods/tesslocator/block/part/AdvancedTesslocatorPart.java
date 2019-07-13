package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.data.TessNet;
import com.latmod.mods.tesslocator.data.TessNetKey;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public abstract class AdvancedTesslocatorPart extends TesslocatorPart
{
	public UUID owner = TessNetKey.UUID_00;
	public boolean isPublic = false;
	public int colors = 0;
	private TessNetKey key = null;

	public AdvancedTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);
		nbt.setLong("owner_most", owner.getMostSignificantBits());
		nbt.setLong("owner_least", owner.getLeastSignificantBits());

		if (isPublic)
		{
			nbt.setBoolean("public", true);
		}

		nbt.setByte("colors", (byte) colors);
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		owner = TessNetKey.uuid(nbt.getLong("owner_most"), nbt.getLong("owner_least"));
		isPublic = nbt.getBoolean("public");
		colors = nbt.getByte("colors") & 0xFF;
	}

	@Override
	public int getColor(int layer)
	{
		if (layer == 0)
		{
			return EnumDyeColor.byMetadata(colors & 0xF).getColorValue();
		}
		else if (layer == 1)
		{
			return EnumDyeColor.byMetadata((colors >> 4) & 0xF).getColorValue();
		}

		return 0xFFFFFF;
	}

	@Override
	public void onPlaced(EntityPlayer player, ItemStack stack)
	{
		owner = player.getUniqueID();
		colors = stack.hasTagCompound() ? stack.getTagCompound().getByte("colors") & 0xFF : 0;
	}

	@Override
	public void clearCache()
	{
		super.clearCache();
		key = null;
	}

	public TessNetKey getKey()
	{
		if (key == null)
		{
			key = new TessNetKey(owner.getMostSignificantBits(), owner.getLeastSignificantBits(), colors);
		}

		return key;
	}

	@Override
	public void drop(World world, BlockPos pos)
	{
		ItemStack stack = new ItemStack(getType().item.get());
		stack.setTagInfo("colors", new NBTTagByte((byte) colors));
		Block.spawnAsEntity(world, pos, stack);
	}

	@Override
	public void onRightClick(EntityPlayer player, EnumHand hand)
	{
		if (hand == EnumHand.MAIN_HAND && !player.getHeldItem(EnumHand.OFF_HAND).isEmpty())
		{
			int dyeA = dyeIndex(player.getHeldItem(EnumHand.MAIN_HAND));
			int dyeB = dyeIndex(player.getHeldItem(EnumHand.OFF_HAND));

			if (dyeA != -1 && dyeB != -1)
			{
				colors = (dyeA | (dyeB << 4)) & 0xFF;
				block.updateContainingBlockInfo();
				block.markDirty();
				block.rerender();

				if (!block.getWorld().isRemote && TessNet.SERVER != null)
				{
					TessNet.SERVER.markDirty();
				}
			}
		}
	}

	private int dyeIndex(ItemStack stack)
	{
		if (stack.getItem() == Items.DYE)
		{
			return EnumDyeColor.byDyeDamage(stack.getMetadata()).getMetadata();
		}

		return -1;
	}
}