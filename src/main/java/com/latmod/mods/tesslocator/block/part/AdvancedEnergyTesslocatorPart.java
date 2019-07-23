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
import java.util.LinkedList;

/**
 * @author LatvianModder
 */
public class AdvancedEnergyTesslocatorPart extends AdvancedTesslocatorPart implements IEnergyStorage
{
	private final LinkedList<AdvancedEnergyTesslocatorPart> temp = new LinkedList<>();
	public int mode = 0;
	public int energy = 0;

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

		if (mode > 0)
		{
			nbt.setByte("output_mode", (byte) mode);
		}

		if (energy > 0)
		{
			nbt.setInteger("energy", energy);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		mode = nbt.getByte("output_mode");
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

		temp.clear();

		for (TesslocatorPart part : TessNet.SERVER.get(getKey()))
		{
			if (part != this && part instanceof AdvancedEnergyTesslocatorPart)
			{
				AdvancedEnergyTesslocatorPart part1 = (AdvancedEnergyTesslocatorPart) part;

				if (part1.mode != 1)
				{
					temp.add(part1);
				}
			}
		}

		if (temp.isEmpty())
		{
			return;
		}

		int p = Math.min(energy / temp.size(), 256);

		if (p > 0 && energy >= p)
		{
			int e = energy;

			for (AdvancedEnergyTesslocatorPart t : temp)
			{
				energy -= t.receiveEnergy(p, false);

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
		if (player.isSneaking() && player.getHeldItem(hand).isEmpty())
		{
			mode = 1 - mode;
			block.markDirty();
			block.rerender();
		}
		else
		{
			super.onRightClick(player, hand);
		}
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		if (maxReceive <= 0 || energy >= getMaxEnergyStored())
		{
			return 0;
		}

		int energyReceived = Math.min(getMaxEnergyStored() - energy, maxReceive);

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

		int energyExtracted = Math.min(energy, maxExtract);

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
		return 4000000;
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