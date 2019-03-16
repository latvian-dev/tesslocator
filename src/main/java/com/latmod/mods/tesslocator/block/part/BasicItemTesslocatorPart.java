package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.mods.tesslocator.block.TileTesslocator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BasicItemTesslocatorPart extends BasicTesslocatorPart
{
	private final BasicItemTesslocatorPart[] temp = new BasicItemTesslocatorPart[5];

	public ItemStack filter = ItemStack.EMPTY;
	public int currentSlot = 0;
	public int cooldown = 0;
	public int currentPart = 0;

	public BasicItemTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public EnumPartType getType()
	{
		return EnumPartType.BASIC_ITEM;
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (!filter.isEmpty())
		{
			nbt.setTag("filter", filter.serializeNBT());
		}

		if (cooldown > 0)
		{
			nbt.setByte("cooldown", (byte) cooldown);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		filter = nbt.hasKey("filter") ? new ItemStack(nbt.getCompoundTag("filter")) : ItemStack.EMPTY;
		cooldown = nbt.getByte("cooldown") & 0xFF;
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
		if (!outputMode)
		{
			return;
		}

		if (cooldown > 0)
		{
			cooldown--;
			return;
		}

		cooldown = 8;

		int tempParts = 0;

		for (TesslocatorPart part : block.parts)
		{
			if (part != this && part instanceof BasicItemTesslocatorPart)
			{
				BasicItemTesslocatorPart part1 = (BasicItemTesslocatorPart) part;

				if (!part1.outputMode)
				{
					temp[tempParts] = part1;
					tempParts++;
				}
			}
		}

		if (tempParts == 0)
		{
			return;
		}

		TileEntity tileEntity = block.getWorld().getTileEntity(block.getPos().offset(facing));

		if (tileEntity == null)
		{
			return;
		}

		IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());

		if (handler == null)
		{
			return;
		}

		int slots = handler.getSlots();

		if (slots <= 0)
		{
			return;
		}
		else if (currentSlot >= slots)
		{
			currentSlot = 0;
		}

		int originalSlot = currentSlot;
		ItemStack stack;

		while (true)
		{
			stack = handler.extractItem(currentSlot, 64, true);

			if (!stack.isEmpty() && ItemFiltersAPI.filter(filter, stack))
			{
				break;
			}

			currentSlot++;

			if (currentSlot >= slots)
			{
				currentSlot = 0;
			}

			if (currentSlot == originalSlot)
			{
				return;
			}
		}

		int i = tempParts == 1 ? 0 : currentPart % tempParts;

		if (ItemFiltersAPI.filter(temp[i].filter, stack))
		{
			TileEntity outEntity = block.getWorld().getTileEntity(block.getPos().offset(temp[i].facing));

			if (outEntity != null)
			{
				IItemHandler outHandler = outEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, temp[i].facing.getOpposite());

				if (outHandler != null)
				{
					ItemStack stack1 = ItemHandlerHelper.insertItem(outHandler, stack, false);

					if (stack1.getCount() != stack.getCount())
					{
						handler.extractItem(currentSlot, stack.getCount() - stack1.getCount(), false);
					}
				}
			}
		}

		currentSlot++;
		currentPart++;
	}
}