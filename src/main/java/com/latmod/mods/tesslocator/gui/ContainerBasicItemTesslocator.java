package com.latmod.mods.tesslocator.gui;

import com.latmod.mods.tesslocator.TesslocatorConfig;
import com.latmod.mods.tesslocator.block.part.BasicItemTesslocatorPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

/**
 * @author LatvianModder
 */
public class ContainerBasicItemTesslocator extends Container
{
	public final BasicItemTesslocatorPart part;

	public ContainerBasicItemTesslocator(BasicItemTesslocatorPart p, EntityPlayer player)
	{
		part = p;

		addSlotToContainer(new SlotItemHandler(part.other, 0, 116, 19)
		{
			@Override
			public int getSlotStackLimit()
			{
				return TesslocatorConfig.basic_item.speed_boost_max;
			}
		});

		addSlotToContainer(new SlotItemHandler(part.other, 1, 152, 19)
		{
			@Override
			public int getSlotStackLimit()
			{
				return TesslocatorConfig.basic_item.stack_boost_max;
			}
		});

		for (int k = 0; k < 3; ++k)
		{
			for (int i1 = 0; i1 < 9; ++i1)
			{
				addSlotToContainer(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 53 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l)
		{
			addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 111));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();

			if (index < 2)
			{
				if (!mergeItemStack(stack1, 2, inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!mergeItemStack(stack1, 0, 2, false))
			{
				return ItemStack.EMPTY;
			}

			if (stack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return stack;
	}

	@Override
	public boolean enchantItem(EntityPlayer player, int id)
	{
		if (id == 0)
		{
			part.mode = (part.mode + 1) % 3;
			part.block.rerender();
			return true;
		}
		else if (id == 1)
		{
			part.inputFilter = ItemHandlerHelper.copyStackWithSize(player.inventory.getItemStack(), 1);
			return true;
		}
		else if (id == 2)
		{
			part.outputFilter = ItemHandlerHelper.copyStackWithSize(player.inventory.getItemStack(), 1);
			return true;
		}

		return false;
	}
}