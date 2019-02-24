package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class AdvancedEnergyTesslocatorPart extends AdvancedTesslocatorPart
{
	public AdvancedEnergyTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public EnumPartType getType()
	{
		return EnumPartType.ADVANCED_ENERGY;
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
	}

	@Override
	public boolean hasCapability(Capability<?> capability)
	{
		return false;
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability)
	{
		return null;
	}

	@Override
	public void update()
	{
	}
}