package com.latmod.mods.tesslocator;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class TesslocatorPart
{
	public final TileTesslocator block;
	public final EnumFacing facing;

	public TesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		block = t;
		facing = f;
	}

	public boolean hasCapability(Capability<?> capability)
	{
		return false;
	}

	@Nullable
	public <T> T getCapability(Capability<T> capability)
	{
		return null;
	}
}