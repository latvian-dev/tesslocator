package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.data.TessNet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BasicEnergyTesslocatorPart extends BasicTesslocatorPart implements IEnergyStorage
{
	private final BasicEnergyTesslocatorPart[] temp = new BasicEnergyTesslocatorPart[5];
	public int energy = 0;

	public BasicEnergyTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public EnumPartType getType()
	{
		return EnumPartType.BASIC_ENERGY;
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (energy > 0)
		{
			nbt.setInteger("energy", energy);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		energy = nbt.getInteger("energy");
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
		return capability == CapabilityEnergy.ENERGY ? (T) this : null;
	}

	@Override
	public void update(TessNet net)
	{
		if (energy <= 0)
		{
			return;
		}

		if (mode == 0)
		{
			TileEntity outEntity = getFacingTile();

			if (outEntity != null)
			{
				IEnergyStorage outHandler = outEntity.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());

				if (outHandler != null && outHandler.canReceive())
				{
					int e = outHandler.receiveEnergy(energy, false);

					if (e > 0)
					{
						energy -= e;
						block.markDirty();
					}
				}
			}

			return;
		}

		int tempParts = 0;

		for (TesslocatorPart part : block.parts)
		{
			if (part != this && part instanceof BasicEnergyTesslocatorPart)
			{
				BasicEnergyTesslocatorPart part1 = (BasicEnergyTesslocatorPart) part;

				if (part1.mode != 1)
				{
					temp[tempParts] = part1;
					tempParts++;
				}
			}
		}

		if (tempParts == 0)
		{
			return;
		}

		int p = Math.min(energy / tempParts, 256);

		if (p > 0 && energy >= p)
		{
			int e = energy;

			for (int i = 0; i < tempParts; i++)
			{
				energy -= temp[i].receiveEnergy(p, false);

				if (energy < p)
				{
					break;
				}
			}

			if (energy != e)
			{
				block.markDirty();
			}
		}
	}

	@Override
	public void onRightClick(EntityPlayer player, EnumHand hand)
	{
		mode = 1 - mode;
		block.markDirty();
		block.rerender();
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		if (maxReceive <= 0 || energy >= getMaxEnergyStored())
		{
			return 0;
		}

		int energyReceived = Math.min(getMaxEnergyStored() - energy, Math.min(256, maxReceive));

		if (!simulate)
		{
			energy += energyReceived;
			block.markDirty();
		}

		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		if (maxExtract <= 0 || energy <= 0)
		{
			return 0;
		}

		int energyExtracted = Math.min(energy, Math.min(256, maxExtract));

		if (!simulate)
		{
			energy -= energyExtracted;
			block.markDirty();
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
		return 32000;
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