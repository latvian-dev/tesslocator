package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public abstract class BasicTesslocatorPart extends TesslocatorPart
{
	public boolean outputMode = false;

	public BasicTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (outputMode)
		{
			nbt.setBoolean("output_mode", true);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		outputMode = nbt.getBoolean("output_mode");
	}

	@Override
	public int getColor(int layer)
	{
		return outputMode ? 0xFFFF9400 : 0xFF00B2FF;
	}

	@Override
	public void onRightClick(EntityPlayer player)
	{
		if (!player.world.isRemote)
		{
			outputMode = !outputMode;
			block.rerender();
		}
	}
}