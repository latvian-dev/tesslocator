package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.itemfilters.api.ItemFiltersAPI;
import com.latmod.mods.tesslocator.block.TileTesslocator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BasicItemTesslocatorPart extends BasicTesslocatorPart
{
	private final BasicItemTesslocatorPart[] temp = new BasicItemTesslocatorPart[5];

	public ItemStack filter = ItemStack.EMPTY;
	public int boost = 0;

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
			nbt.setByte("current_slot", (byte) currentPart);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		filter = nbt.hasKey("filter") ? new ItemStack(nbt.getCompoundTag("filter")) : ItemStack.EMPTY;
		boost = nbt.getByte("boost") & 0xFF;
		currentSlot = nbt.getInteger("current_slot");
		cooldown = nbt.getByte("cooldown") & 0xFF;
		currentPart = nbt.getByte("current_part") & 0xFF;
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

		cooldown = 17 - boost;

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
				if (boost < 16)
				{
					boost += add;

					if (boost > 16)
					{
						boost = 16;
					}

					stack1.shrink(1);
				}

				player.sendStatusMessage(new TextComponentString(boost + " / 16"), true);
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
}