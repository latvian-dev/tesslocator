package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.Tesslocator;
import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.data.TessNet;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class TesslocatorPart
{
	public final TileTesslocator block;
	public final EnumFacing facing;
	private TileEntity facingTile;

	public TesslocatorPart(TileTesslocator t, EnumFacing f)
	{
		block = t;
		facing = f;
		facingTile = null;
	}

	public abstract EnumPartType getType();

	public void writeData(NBTTagCompound nbt)
	{
	}

	public void readData(NBTTagCompound nbt)
	{
	}

	public void clearCache()
	{
		facingTile = null;
	}

	public abstract boolean hasCapability(Capability<?> capability);

	@Nullable
	public abstract <T> T getCapability(Capability<T> capability);

	public abstract void update(TessNet net);

	public int getColor(int layer)
	{
		return 0xFFFFFFFF;
	}

	public void onRightClick(EntityPlayer player, EnumHand hand)
	{
	}

	public void onPlaced(EntityPlayer player, ItemStack stack)
	{
	}

	public void drop(World world, BlockPos pos)
	{
		Block.spawnAsEntity(world, pos, new ItemStack(getType().item.get()));
	}

	public final void openGui(EntityPlayer player)
	{
		player.openGui(Tesslocator.INSTANCE, facing.getIndex(), block.getWorld(), block.getPos().getX(), block.getPos().getY(), block.getPos().getZ());
	}

	@Nullable
	public Container getGuiContainer(EntityPlayer player)
	{
		return null;
	}

	@Nullable
	public Object getGuiScreen(Container container)
	{
		return null;
	}

	@Nullable
	public TileEntity getFacingTile()
	{
		if (facingTile != null && !facingTile.isInvalid())
		{
			return facingTile;
		}

		facingTile = block.getWorld().getTileEntity(block.getPos().offset(facing));
		return facingTile;
	}
}