package com.latmod.mods.tesslocator.gui;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface IGhostFluidHandler
{
	void setGhostFluid(int slot, @Nullable FluidStack stack);
}