package com.latmod.mods.tesslocator.gui;

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

		addSlotToContainer(new SlotItemHandler(part.other, 0, 143, 10));
		addSlotToContainer(new SlotItemHandler(part.other, 1, 143, 28));

		for (int y = 0; y < 2; y++)
		{
			for (int x = 0; x < 4; x++)
			{
				addSlotToContainer(new SlotItemHandler(part, x + y * 4, 53 + x * 18, 10 + y * 18));
			}
		}

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

			if (index < 10)
			{
				if (!mergeItemStack(stack1, 10, inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!mergeItemStack(stack1, 0, 10, false))
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