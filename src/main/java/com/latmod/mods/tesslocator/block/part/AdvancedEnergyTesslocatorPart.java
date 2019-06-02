package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.data.EnergyData;
import com.latmod.mods.tesslocator.data.TessNet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

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
		return capability == CapabilityEnergy.ENERGY;
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability)
	{
		return capability == CapabilityEnergy.ENERGY ? (T) TessNet.SERVER.getEnergyData(getKey()) : null;
	}

	@Override
	public void update()
	{
		EnergyData data = TessNet.SERVER.getEnergyData(getKey());

		TileEntity outEntity = block.getWorld().getTileEntity(block.getPos().offset(facing));

		if (outEntity != null)
		{
			IEnergyStorage outHandler = outEntity.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());

			if (outHandler != null && outHandler.canReceive())
			{
				int e = outHandler.receiveEnergy(data.energy, false);

				if (e > 0)
				{
					data.energy -= e;
					data.net.markDirty();
				}
			}
		}
	}
}