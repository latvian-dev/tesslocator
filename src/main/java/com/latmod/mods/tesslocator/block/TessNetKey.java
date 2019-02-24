package com.latmod.mods.tesslocator.block;

import com.latmod.mods.tesslocator.block.part.AdvancedTesslocatorPart;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class TessNetKey
{
	public static final UUID UUID_00 = new UUID(0L, 0L);

	public final UUID owner;
	public final int colors;

	public TessNetKey(AdvancedTesslocatorPart part)
	{
		owner = part.isPublic ? UUID_00 : part.owner;
		colors = part.colors;
	}

	public int hashCode()
	{
		return owner.hashCode() * 31 + colors;
	}

	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		else if (o instanceof TessNetKey)
		{
			TessNetKey k = (TessNetKey) o;
			return colors == k.colors && owner.getMostSignificantBits() == k.owner.getMostSignificantBits() && owner.getLeastSignificantBits() == k.owner.getLeastSignificantBits();
		}

		return false;
	}

	public String toString()
	{
		return String.format("%s+#%02X", owner, colors);
	}
}