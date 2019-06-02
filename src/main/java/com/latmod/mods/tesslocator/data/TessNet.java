package com.latmod.mods.tesslocator.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class TessNet
{
	public static TessNet SERVER = null;

	public boolean isDirty = false;
	private final Map<TessNetKey, EnergyData> energyData = new HashMap<>();

	public void markDirty()
	{
		isDirty = true;
	}

	private NBTTagList writeToList(Collection<? extends TesslocatorData> values)
	{
		NBTTagList list = new NBTTagList();

		for (TesslocatorData data : values)
		{
			if (data.shouldSave())
			{
				NBTTagCompound nbt = new NBTTagCompound();
				data.write(nbt);
				data.key.write(nbt);
				list.appendTag(nbt);
			}
		}

		return list;
	}

	public void write(NBTTagCompound nbt)
	{
		nbt.setTag("energy", writeToList(energyData.values()));
	}

	public void read(NBTTagCompound nbt)
	{
	}

	public EnergyData getEnergyData(TessNetKey key)
	{
		EnergyData data = energyData.get(key);

		if (data == null)
		{
			data = new EnergyData(this, key);
			energyData.put(key, data);
		}

		return data;
	}
}