package com.latmod.mods.tesslocator.client;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.block.part.TesslocatorPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class TesslocatorGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id >= 0 && id < 6)
		{
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

			if (tileEntity instanceof TileTesslocator)
			{
				TesslocatorPart part = ((TileTesslocator) tileEntity).parts[id];

				if (part != null)
				{
					return part.getGuiContainer(player);
				}
			}
		}

		return null;
	}

	@Nullable
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id >= 0 && id < 6)
		{
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

			if (tileEntity instanceof TileTesslocator)
			{
				TesslocatorPart part = ((TileTesslocator) tileEntity).parts[id];

				if (part != null)
				{
					Container container = part.getGuiContainer(player);

					if (container != null)
					{
						return part.getGuiScreen(container);
					}
				}
			}
		}

		return null;
	}
}
