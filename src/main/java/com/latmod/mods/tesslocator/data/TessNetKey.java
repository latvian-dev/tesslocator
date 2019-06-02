package com.latmod.mods.tesslocator.data;

import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public final class TessNetKey
{
	public static final UUID UUID_00 = new UUID(0L, 0L);

	public static UUID uuid(long m, long l)
	{
		if (m == 0L && l == 0L)
		{
			return UUID_00;
		}

		return new UUID(m, l);
	}

	public final long ownerMost;
	public final long ownerLeast;
	public final int colors;

	public TessNetKey(long m, long l, int c)
	{
		ownerMost = m;
		ownerLeast = l;
		colors = c & 0xFF;
	}

	public TessNetKey(NBTTagCompound nbt)
	{
		this(nbt.getLong("owner_most"), nbt.getLong("owner_least"), nbt.getByte("colors"));
	}

	public void write(NBTTagCompound nbt)
	{
		nbt.setLong("owner_most", ownerMost);
		nbt.setLong("owner_least", ownerLeast);
		nbt.setByte("colors", (byte) colors);
	}

	public UUID getUUID()
	{
		return uuid(ownerMost, ownerLeast);
	}

	public int getColorA()
	{
		return colors & 0xF;
	}

	public int getColorB()
	{
		return (colors >> 4) & 0xF;
	}

	@Override
	public int hashCode()
	{
		int h = colors;
		h = h * 31 + Long.hashCode(ownerMost);
		h = h * 31 + Long.hashCode(ownerLeast);
		return h;
	}

	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj instanceof TessNetKey)
		{
			TessNetKey key = (TessNetKey) obj;
			return ownerMost == key.ownerMost && ownerLeast == key.ownerLeast && colors == key.colors;
		}

		return false;
	}

	public String toString()
	{
		return UUIDTypeAdapter.fromUUID(getUUID()) + ":" + Integer.toHexString(getColorA()) + Integer.toHexString(getColorB());
	}
}