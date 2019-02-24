package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class AdvancedItemTesslocatorPart extends AdvancedTesslocatorPart
{
	public ItemStack filter = ItemStack.EMPTY;

	public AdvancedItemTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public EnumPartType getType()
	{
		return EnumPartType.ADVANCED_ITEM;
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (!filter.isEmpty())
		{
			nbt.setTag("filter", filter.serializeNBT());
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		filter = nbt.hasKey("filter") ? new ItemStack(nbt.getCompoundTag("filter")) : ItemStack.EMPTY;
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