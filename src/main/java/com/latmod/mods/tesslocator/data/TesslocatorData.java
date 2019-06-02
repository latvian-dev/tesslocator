package com.latmod.mods.tesslocator.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author LatvianModder
 */
public class TesslocatorData
{
	public final TessNet net;
	public final TessNetKey key;

	public TesslocatorData(TessNet n, TessNetKey k)
	{
		net = n;
		key = k;
	}

	public void write(NBTTagCompound nbt)
	{
	}

	public void read(NBTTagCompound nbt)
	{
	}

	public boolean shouldSave()
	{
		return false;
	}
}