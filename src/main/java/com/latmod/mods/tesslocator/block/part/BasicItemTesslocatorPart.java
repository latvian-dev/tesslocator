package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.mods.tesslocator.TesslocatorConfig;
import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.gui.ContainerBasicItemTesslocator;
import com.latmod.mods.tesslocator.gui.GuiBasicItemTesslocator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * @author LatvianModder
 */
public class BasicItemTesslocatorPart extends BasicTesslocatorPart implements IItemHandlerModifiable
{
	private final BasicItemTesslocatorPart[] temp = new BasicItemTesslocatorPart[5];
	public static boolean ignoreMarkDirty = false;

	public ItemStack inputFilter = ItemStack.EMPTY;
	public ItemStack outputFilter = ItemStack.EMPTY;
	public final ItemStack[] buffer = new ItemStack[8];

	public final ItemStackHandler other = new ItemStackHandler(2)
	{
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack)
		{
			if (slot == 0)
			{
				return stack.getItem() == Items.GLOWSTONE_DUST;
			}
			else if (slot == 1)
			{
				return stack.getItem() == Items.DIAMOND;
			}

			return false;
		}

		@Override
		public int getStackLimit(int slot, ItemStack stack)
		{
			if (slot == 0)
			{
				return TesslocatorConfig.basic_item.speed_boost_max;
			}
			else if (slot == 1)
			{
				return TesslocatorConfig.basic_item.stack_boost_max;
			}

			return 0;
		}

		@Override
		protected void onContentsChanged(int slot)
		{
			if (!ignoreMarkDirty)
			{
				block.markDirty();
			}
		}
	};

	public int currentSlot = 0;
	public int cooldown = 0;
	public int currentPart = 0;

	public BasicItemTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
		Arrays.fill(buffer, ItemStack.EMPTY);
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

		if (!inputFilter.isEmpty())
		{
			nbt.setTag("input_filter", inputFilter.serializeNBT());
		}

		if (!outputFilter.isEmpty())
		{
			nbt.setTag("output_filter", outputFilter.serializeNBT());
		}

		NBTTagList otherTag = new NBTTagList();

		for (int i = 0; i < other.getSlots(); i++)
		{
			if (!other.getStackInSlot(i).isEmpty())
			{
				NBTTagCompound nbt1 = other.getStackInSlot(i).serializeNBT();
				nbt1.setByte("slot", (byte) i);
				otherTag.appendTag(nbt1);
			}
		}

		if (!otherTag.isEmpty())
		{
			nbt.setTag("other", otherTag);
		}

		if (currentSlot > 0)
		{
			nbt.setInteger("current_slot", currentSlot);
		}

		if (cooldown > 0)
		{
			nbt.setByte("cooldown", (byte) cooldown);
		}

		if (currentPart > 0)
		{
			nbt.setByte("current_part", (byte) currentPart);
		}

		NBTTagList bufferTag = new NBTTagList();

		for (int i = 0; i < buffer.length; i++)
		{
			if (!buffer[i].isEmpty())
			{
				NBTTagCompound nbt1 = buffer[i].serializeNBT();
				nbt1.setByte("slot", (byte) i);
				bufferTag.appendTag(nbt1);
			}
		}

		if (!bufferTag.isEmpty())
		{
			nbt.setTag("buffer", bufferTag);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		inputFilter = nbt.hasKey("input_filter") ? new ItemStack(nbt.getCompoundTag("input_filter")) : ItemStack.EMPTY;
		outputFilter = nbt.hasKey("output_filter") ? new ItemStack(nbt.getCompoundTag("output_filter")) : ItemStack.EMPTY;
		other.setSize(2);

		NBTTagList otherTag = nbt.getTagList("other", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < otherTag.tagCount(); i++)
		{
			NBTTagCompound nbt1 = otherTag.getCompoundTagAt(i);
			ItemStack stack = new ItemStack(nbt1);

			if (!stack.isEmpty())
			{
				ignoreMarkDirty = true;
				other.setStackInSlot(nbt1.getByte("slot"), stack);
				ignoreMarkDirty = false;
			}
		}

		if (otherTag.isEmpty())
		{
			int speedBoost = nbt.getByte("boost") & 0xFF;
			int stackBoost = nbt.getByte("stack") & 0xFF;

			if (speedBoost > 0)
			{
				other.setStackInSlot(0, new ItemStack(Items.GLOWSTONE_DUST, speedBoost));
			}

			if (stackBoost > 0)
			{
				other.setStackInSlot(1, new ItemStack(Items.DIAMOND, stackBoost));
			}
		}

		currentSlot = nbt.getInteger("current_slot");

		if (currentSlot < 0)
		{
			currentSlot = 0;
		}

		cooldown = nbt.getByte("cooldown") & 0xFF;
		currentPart = nbt.getByte("current_part") & 0xFF;

		Arrays.fill(buffer, ItemStack.EMPTY);

		NBTTagList bufferTag = nbt.getTagList("buffer", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < bufferTag.tagCount(); i++)
		{
			NBTTagCompound nbt1 = bufferTag.getCompoundTagAt(i);
			ItemStack stack = new ItemStack(nbt1);

			if (!stack.isEmpty())
			{
				buffer[nbt1.getByte("slot")] = stack;
			}
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) this : null;
	}

	@Override
	public void update()
	{
		if (mode == 0)
		{
			return;
		}

		if (cooldown > 0)
		{
			cooldown--;
			return;
		}

		cooldown = TesslocatorConfig.basic_item.speed_boost_starting - (int) (other.getStackInSlot(0).getCount() * TesslocatorConfig.basic_item.speed_boost_multiplier);

		int tempParts = 0;

		for (TesslocatorPart part : block.parts)
		{
			if (part != this && part instanceof BasicItemTesslocatorPart)
			{
				BasicItemTesslocatorPart part1 = (BasicItemTesslocatorPart) part;

				if (part1.mode != 1)
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

		currentSlot = currentSlot % slots;
		int originalSlot = currentSlot;

		if (originalSlot < 0)
		{
			originalSlot = 0;
		}

		ItemStack stack;

		while (true)
		{
			stack = handler.extractItem(currentSlot, 64, true);

			if (!stack.isEmpty() && ItemFiltersAPI.filter(outputFilter, stack))
			{
				break;
			}

			currentSlot = (currentSlot + 1) % slots;

			if (currentSlot == originalSlot)
			{
				return;
			}
		}

		int i = currentPart % tempParts;

		if (ItemFiltersAPI.filter(temp[i].inputFilter, stack))
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

	@Override
	public void drop(World world, BlockPos pos)
	{
		super.drop(world, pos);

		for (int i = 0; i < other.getSlots(); i++)
		{
			Block.spawnAsEntity(world, pos, other.getStackInSlot(i));
		}

		for (ItemStack stack : buffer)
		{
			Block.spawnAsEntity(world, pos, stack);
		}
	}

	@Override
	public int getSlots()
	{
		return buffer.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return buffer[slot];
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (stack.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		ItemStack existing = buffer[slot];

		int limit = getStackLimit(slot, stack);

		if (!existing.isEmpty())
		{
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
			{
				return stack;
			}

			limit -= existing.getCount();
		}

		if (limit <= 0)
		{
			return stack;
		}

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate)
		{
			if (existing.isEmpty())
			{
				buffer[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
			}
			else
			{
				existing.grow(reachedLimit ? limit : stack.getCount());
			}

			block.markDirty();
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if (amount == 0)
		{
			return ItemStack.EMPTY;
		}

		ItemStack existing = buffer[slot];

		if (existing.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract)
		{
			if (!simulate)
			{
				buffer[slot] = ItemStack.EMPTY;
				block.markDirty();
			}

			return existing;
		}

		if (!simulate)
		{
			buffer[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract);
			block.markDirty();
		}

		return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
	}

	protected int getStackLimit(int slot, ItemStack stack)
	{
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		buffer[slot] = stack;
	}

	@Override
	@Nullable
	public Container getGuiContainer(EntityPlayer player)
	{
		return new ContainerBasicItemTesslocator(this, player);
	}

	@Override
	@Nullable
	public Object getGuiScreen(Container container)
	{
		return new GuiBasicItemTesslocator((ContainerBasicItemTesslocator) container);
	}
}