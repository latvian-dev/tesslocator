package com.latmod.mods.tesslocator.block;

import com.latmod.mods.tesslocator.block.part.EnumPartType;
import com.latmod.mods.tesslocator.block.part.TesslocatorPart;
import com.latmod.mods.tesslocator.data.TessNet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * @author LatvianModder
 */
public class TileTesslocator extends TileEntity implements ITickable
{
	private boolean isDirty = false;
	public final TesslocatorPart[] parts = new TesslocatorPart[6];

	public void writeData(NBTTagCompound nbt)
	{
		NBTTagList list = new NBTTagList();

		for (TesslocatorPart part : parts)
		{
			if (part != null)
			{
				NBTTagCompound nbt1 = new NBTTagCompound();
				part.writeData(nbt1);
				nbt1.setByte("side", (byte) part.facing.getIndex());
				nbt1.setString("type", part.getType().id);
				list.appendTag(nbt1);
			}
		}

		nbt.setTag("parts", list);
	}

	public void readData(NBTTagCompound nbt)
	{
		Arrays.fill(parts, null);

		NBTTagList list = nbt.getTagList("parts", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
			int s = nbt1.getByte("side");

			if (s >= 0 && s < 6)
			{
				EnumPartType type = EnumPartType.byID(nbt1.getString("type"));

				if (type != null)
				{
					parts[s] = type.provider.createPart(this, EnumFacing.VALUES[s]);
					parts[s].readData(nbt1);
					parts[s].clearCache();
				}
			}
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return facing != null && world != null && !world.isRemote && parts[facing.getIndex()] != null && parts[facing.getIndex()].hasCapability(capability) || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		Object object = world != null && !world.isRemote && facing != null && parts[facing.getIndex()] != null ? parts[facing.getIndex()].getCapability(capability) : null;
		return object != null ? (T) object : super.getCapability(capability, facing);
	}

	@Override
	public void updateContainingBlockInfo()
	{
		super.updateContainingBlockInfo();

		for (TesslocatorPart part : parts)
		{
			if (part != null)
			{
				part.clearCache();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		writeData(nbt);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		readData(nbt);
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		readFromNBT(tag);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeData(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, SPacketUpdateTileEntity packet)
	{
		readData(packet.getNbtCompound());
		rerender();
	}

	@Override
	public void markDirty()
	{
		isDirty = true;
	}

	@Override
	public void update()
	{
		int p = 0;

		for (TesslocatorPart part : parts)
		{
			if (part != null)
			{
				p++;

				if (!world.isRemote && TessNet.SERVER != null)
				{
					part.update();
				}
			}
		}

		if (p == 0)
		{
			world.setBlockToAir(pos);
		}

		if (isDirty)
		{
			world.markChunkDirty(pos, this);
			isDirty = false;
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return oldState.getBlock() != newSate.getBlock();
	}

	public void rerender()
	{
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 11);
	}
}
