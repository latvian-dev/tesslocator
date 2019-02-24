package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public abstract class AdvancedTesslocatorPart extends TesslocatorPart
{
	public UUID owner = new UUID(0L, 0L);
	public boolean isPublic = false;
	public int colors = 0;

	public AdvancedTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);
		nbt.setUniqueId("owner", owner);

		if (isPublic)
		{
			nbt.setBoolean("public", true);
		}

		nbt.setByte("colors", (byte) colors);
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		owner = nbt.getUniqueId("owner");
		isPublic = nbt.getBoolean("public");
		colors = nbt.getInteger("colors") & 0xFF;
	}

	@Override
	public int getColor(int layer)
	{
		return EnumDyeColor.byMetadata((colors >> (layer * 4)) & 0xF).getColorValue();
	}

	@Override
	public void onRightClick(EntityPlayer player)
	{
	}
}