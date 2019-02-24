package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BasicFluidTesslocatorPart extends BasicTesslocatorPart
{
	public FluidStack filter = null;

	public BasicFluidTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public EnumPartType getType()
	{
		return EnumPartType.BASIC_FLUID;
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (filter != null)
		{
			nbt.setTag("filter", filter.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		filter = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("filter"));
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