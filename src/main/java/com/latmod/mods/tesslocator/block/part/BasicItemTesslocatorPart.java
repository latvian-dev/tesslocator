package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.mods.tesslocator.TesslocatorConfig;
import com.latmod.mods.tesslocator.block.TileTesslocator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * @author LatvianModder
 */
public class BasicItemTesslocatorPart extends BasicTesslocatorPart implements IItemHandlerModifiable
{
	private final BasicItemTesslocatorPart[] temp = new BasicItemTesslocatorPart[5];

	public ItemStack filter = ItemStack.EMPTY;
	public int boost = 0;
	public final ItemStack[] buffer = new ItemStack[8];

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

		if (!filter.isEmpty())
		{
			nbt.setTag("filter", filter.serializeNBT());
		}

		if (boost > 0)
		{
			nbt.setByte("boost", (byte) boost);
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

		NBTTagList list = new NBTTagList();

		for (int i = 0; i < buffer.length; i++)
		{
			if (!buffer[i].isEmpty())
			{
				NBTTagCompound nbt1 = buffer[i].serializeNBT();
				nbt1.setByte("slot", (byte) i);
				list.appendTag(nbt1);
			}
		}

		if (!list.isEmpty())
		{
			nbt.setTag("buffer", list);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		filter = nbt.hasKey("filter") ? new ItemStack(nbt.getCompoundTag("filter")) : ItemStack.EMPTY;
		boost = nbt.getByte("boost") & 0xFF;
		currentSlot = nbt.getInteger("current_slot");

		if (currentSlot < 0)
		{
			currentSlot = 0;
		}

		cooldown = nbt.getByte("cooldown") & 0xFF;
		currentPart = nbt.getByte("current_part") & 0xFF;

		Arrays.fill(buffer, ItemStack.EMPTY);

		NBTTagList list = nbt.getTagList("buffer", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
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
		if (!outputMode)
		{
			return;
		}

		if (cooldown > 0)
		{
			cooldown--;
			return;
		}

		cooldown = TesslocatorConfig.general.boost_starting - boost * TesslocatorConfig.general.boost_multiplier;

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

			if (!stack.isEmpty() && ItemFiltersAPI.filter(filter, stack))
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

	@Override
	public void onRightClick(EntityPlayer player, EnumHand hand)
	{
		ItemStack stack1 = player.getHeldItem(hand);

		if (!stack1.isEmpty())
		{
			IntOpenHashSet ores = new IntOpenHashSet(OreDictionary.getOreIDs(stack1));

			int add = 0;

			if (ores.contains(OreDictionary.getOreID("dustGlowstone")))
			{
				add = 1;
			}
			else if (ores.contains(OreDictionary.getOreID("glowstone")))
			{
				add = 4;
			}

			if (add > 0)
			{
				if (boost < TesslocatorConfig.general.boost_max)
				{
					boost += add;

					if (boost > TesslocatorConfig.general.boost_max)
					{
						boost = TesslocatorConfig.general.boost_max;
					}

					if (!player.capabilities.isCreativeMode)
					{
						stack1.shrink(1);
					}
				}

				ITextComponent component = new TextComponentString(boost + " / " + TesslocatorConfig.general.boost_max);
				component.getStyle().setColor(TextFormatting.GOLD);
				player.sendStatusMessage(component, true);
				return;
			}
		}

		if (!player.world.isRemote)
		{
			outputMode = !outputMode;
			block.rerender();
		}
	}

	@Override
	public void drop(World world, BlockPos pos)
	{
		super.drop(world, pos);

		if (boost > 0)
		{
			Block.spawnAsEntity(world, pos, new ItemStack(Items.GLOWSTONE_DUST, boost));
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
}