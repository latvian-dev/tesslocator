package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.TesslocatorConfig;
import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.gui.ContainerBasicFluidTesslocator;
import com.latmod.mods.tesslocator.gui.GuiBasicFluidTesslocator;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BasicFluidTesslocatorPart extends BasicTesslocatorPart
{
	private final BasicFluidTesslocatorPart[] temp = new BasicFluidTesslocatorPart[5];
	public static boolean ignoreMarkDirty = false;

	public FluidStack inputFilter = null;
	public FluidStack outputFilter = null;

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
				return TesslocatorConfig.basic_fluid.speed_boost_max;
			}
			else if (slot == 1)
			{
				return TesslocatorConfig.basic_fluid.stack_boost_max;
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

	public int cooldown = 0;
	public int currentPart = 0;

	public BasicFluidTesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		super(t, f);
	}

	@Override
	public EnumPartType getType()
	{
		return EnumPartType.BASIC_FLUID;
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (inputFilter != null)
		{
			inputFilter.amount = 1000;
			nbt.setTag("input_filter", inputFilter.writeToNBT(new NBTTagCompound()));
		}

		if (outputFilter != null)
		{
			outputFilter.amount = 1000;
			nbt.setTag("output_filter", outputFilter.writeToNBT(new NBTTagCompound()));
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

		if (cooldown > 0)
		{
			nbt.setByte("cooldown", (byte) cooldown);
		}

		if (currentPart > 0)
		{
			nbt.setByte("current_part", (byte) currentPart);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);
		inputFilter = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("input_filter"));
		outputFilter = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("output_filter"));

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

		cooldown = nbt.getByte("cooldown") & 0xFF;
		currentPart = nbt.getByte("current_part") & 0xFF;
	}

	@Override
	public boolean hasCapability(Capability<?> capability)
	{
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability)
	{
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
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

		cooldown = TesslocatorConfig.basic_fluid.speed_boost_starting - (int) (other.getStackInSlot(0).getCount() * TesslocatorConfig.basic_fluid.speed_boost_multiplier);
		moveFluid();
	}

	private void moveFluid()
	{
		int tempParts = 0;

		for (TesslocatorPart part : block.parts)
		{
			if (part != this && part instanceof BasicFluidTesslocatorPart)
			{
				BasicFluidTesslocatorPart part1 = (BasicFluidTesslocatorPart) part;

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

		TileEntity tileEntity = getFacingTile();

		if (tileEntity == null)
		{
			return;
		}

		IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());

		if (handler == null)
		{
			return;
		}

		int stackBoost = other.getStackInSlot(1).getCount() + 1;

		for (int x = 0; x < stackBoost; x++)
		{
			FluidStack stack;

			if (outputFilter == null)
			{
				stack = handler.drain(8000, false);
			}
			else
			{
				outputFilter.amount = 8000;
				stack = handler.drain(outputFilter, false);
				outputFilter.amount = 1000;
			}

			if (stack == null)
			{
				return;
			}

			int i = currentPart % tempParts;

			if (temp[i].inputFilter == null || temp[i].inputFilter.isFluidEqual(stack))
			{
				TileEntity outEntity = temp[i].getFacingTile();

				if (outEntity != null)
				{
					IFluidHandler outHandler = outEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, temp[i].facing.getOpposite());

					if (outHandler != null)
					{
						int fill = outHandler.fill(stack, true);

						if (fill > 0)
						{
							if (outputFilter == null)
							{
								handler.drain(fill, true);
							}
							else
							{
								outputFilter.amount = fill;
								handler.drain(outputFilter, true);
								outputFilter.amount = 1000;
							}
						}
					}
				}
			}

			currentPart++;

			if (currentPart >= 256)
			{
				currentPart = 0;
			}
		}
	}

	@Override
	public void drop(World world, BlockPos pos)
	{
		super.drop(world, pos);

		for (int i = 0; i < other.getSlots(); i++)
		{
			Block.spawnAsEntity(world, pos, other.getStackInSlot(i));
		}
	}

	@Override
	@Nullable
	public Container getGuiContainer(EntityPlayer player)
	{
		return new ContainerBasicFluidTesslocator(this, player);
	}

	@Override
	@Nullable
	public Object getGuiScreen(Container container)
	{
		return new GuiBasicFluidTesslocator((ContainerBasicFluidTesslocator) container);
	}
}