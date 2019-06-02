package com.latmod.mods.tesslocator.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author LatvianModder
 */
public class EnergyData extends TesslocatorData implements IEnergyStorage
{
	public int energy;

	public EnergyData(TessNet n, TessNetKey k)
	{
		super(n, k);
		energy = 0;
	}

	@Override
	public void write(NBTTagCompound nbt)
	{
		nbt.setInteger("energy", energy);
	}

	@Override
	public void read(NBTTagCompound nbt)
	{
		energy = nbt.getInteger("energy");
	}

	@Override
	public boolean shouldSave()
	{
		return energy > 0;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		int energyReceived = Math.min(getMaxEnergyStored() - energy, maxReceive);

		if (!simulate)
		{
			energy += energyReceived;
			net.markDirty();
		}

		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		int energyExtracted = Math.min(energy, maxExtract);

		if (!simulate)
		{
			energy -= energyExtracted;
			net.markDirty();
		}

		return energyExtracted;
	}

	@Override
	public int getEnergyStored()
	{
		return energy;
	}

	@Override
	public int getMaxEnergyStored()
	{
		return 2048000000;
	}

	@Override
	public boolean canExtract()
	{
		return true;
	}

	@Override
	public boolean canReceive()
	{
		return true;
	}
}