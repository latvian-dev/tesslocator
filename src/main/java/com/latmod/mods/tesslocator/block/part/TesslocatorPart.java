package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class TesslocatorPart implements ITickable
{
	public final TileTesslocator block;
	public final EnumFacing facing;

	public TesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		block = t;
		facing = f;
	}

	public abstract EnumPartType getType();

	public void writeData(NBTTagCompound nbt)
	{
	}

	public void readData(NBTTagCompound nbt)
	{
	}

	public abstract boolean hasCapability(Capability<?> capability);

	@Nullable
	public abstract <T> T getCapability(Capability<T> capability);

	public int getColor(int layer)
	{
		return 0xFFFFFFFF;
	}

	public void onRightClick(EntityPlayer player, EnumHand hand)
	{
	}

	public void onPlaced(EntityPlayer player, ItemStack stack)
	{
	}
}