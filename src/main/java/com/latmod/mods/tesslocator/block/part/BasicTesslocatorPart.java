package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public abstract class BasicTesslocatorPart extends TesslocatorPart
{
	public int mode = 0;

	public BasicTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (mode > 0)
		{
			nbt.setByte("output_mode", (byte) mode);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		mode = nbt.getByte("output_mode");
	}

	@Override
	public int getColor(int layer)
	{
		if (mode == 2)
		{
			return layer == 0 ? 0xFFFF9400 : 0xFF00B2FF;
		}

		return mode == 1 ? 0xFFFF9400 : 0xFF00B2FF;
	}

	@Override
	public void onRightClick(EntityPlayer player, EnumHand hand)
	{
		openGui(player);
	}
}