package com.latmod.mods.tesslocator.gui;

import com.latmod.mods.tesslocator.TesslocatorConfig;
import com.latmod.mods.tesslocator.block.part.BasicFluidTesslocatorPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ContainerBasicFluidTesslocator extends Container implements IGhostFluidHandler
{
	public final BasicFluidTesslocatorPart part;

	public ContainerBasicFluidTesslocator(BasicFluidTesslocatorPart p, EntityPlayer player)
	{
		part = p;

		addSlotToContainer(new SlotItemHandler(part.other, 0, 116, 19)
		{
			@Override
			public int getSlotStackLimit()
			{
				return TesslocatorConfig.basic_fluid.speed_boost_max;
			}
		});

		addSlotToContainer(new SlotItemHandler(part.other, 1, 152, 19)
		{
			@Override
			public int getSlotStackLimit()
			{
				return TesslocatorConfig.basic_fluid.stack_boost_max;
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
			part.block.markDirty();
			part.block.rerender();
			return true;
		}
		else if (id == 1)
		{
			part.inputFilter = copyBucket(getFromItem(player.inventory.getItemStack()));
			part.block.markDirty();
			return true;
		}
		else if (id == 2)
		{
			part.outputFilter = copyBucket(getFromItem(player.inventory.getItemStack()));
			part.block.markDirty();
			return true;
		}

		return false;
	}

	@Nullable
	public static FluidStack getFromItem(ItemStack stack)
	{
		IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

		if (handler == null)
		{
			handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		}

		if (handler != null)
		{
			for (IFluidTankProperties tankProperties : handler.getTankProperties())
			{
				if (tankProperties.getContents() != null && tankProperties.getContents().getFluid() != null && tankProperties.getContents().amount > 0)
				{
					return tankProperties.getContents();
				}
			}
		}

		return null;
	}

	@Nullable
	public static FluidStack copyBucket(@Nullable FluidStack stack)
	{
		if (stack == null)
		{
			return null;
		}

		FluidStack stack1 = stack.copy();
		stack1.amount = 1000;
		return stack1;
	}

	@Override
	public void setGhostFluid(int slot, @Nullable FluidStack stack)
	{
		if (slot == 0)
		{
			part.inputFilter = copyBucket(stack);
			part.block.markDirty();
		}
		else if (slot == 1)
		{
			part.outputFilter = copyBucket(stack);
			part.block.markDirty();
		}
	}
}