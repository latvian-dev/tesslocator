package com.latmod.mods.tesslocator.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiFunction;

/**
 * @author LatvianModder
 */
public class TessNet
{
	public static TessNet SERVER = null;

	public boolean isDirty = false;
	private HashMap<TessNetKey, EnergyData> energyData = new HashMap<>();

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

	private <T extends TesslocatorData> HashMap<TessNetKey, T> readFromList(NBTTagCompound nbt, String key, BiFunction<TessNet, TessNetKey, T> provider)
	{
		NBTTagList list = nbt.getTagList(key, Constants.NBT.TAG_COMPOUND);

		HashMap<TessNetKey, T> map = new HashMap<>(list.tagCount());

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
			TessNetKey tkey = new TessNetKey(nbt1);
			T t = provider.apply(this, tkey);
			t.read(nbt1);
			map.put(tkey, t);
		}

		return map;
	}

	public void write(NBTTagCompound nbt)
	{
		nbt.setTag("energy", writeToList(energyData.values()));
	}

	public void read(NBTTagCompound nbt)
	{
		energyData = readFromList(nbt, "energy", EnergyData::new);
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