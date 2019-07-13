package com.latmod.mods.tesslocator.data;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.block.part.AdvancedTesslocatorPart;
import com.latmod.mods.tesslocator.block.part.TesslocatorPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class TessNet
{
	public static TessNet SERVER = null;

	private boolean isDirty = true;
	private AdvancedTesslocatorPart[] advancedParts = null;
	private Map<TessNetKey, AdvancedTesslocatorPart[]> keyMap = new HashMap<>();

	public void markDirty()
	{
		isDirty = true;
	}

	public void update()
	{
		if (!isDirty)
		{
			return;
		}

		advancedParts = null;
		keyMap = null;
		isDirty = false;
	}

	public AdvancedTesslocatorPart[] getAll()
	{
		if (advancedParts != null)
		{
			return advancedParts;
		}

		ArrayList<AdvancedTesslocatorPart> list = new ArrayList<>();

		for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds)
		{
			for (TileEntity tileEntity : world.loadedTileEntityList)
			{
				if (tileEntity instanceof TileTesslocator && !tileEntity.isInvalid())
				{
					for (TesslocatorPart part : ((TileTesslocator) tileEntity).parts)
					{
						if (part instanceof AdvancedTesslocatorPart)
						{
							list.add((AdvancedTesslocatorPart) part);
						}
					}
				}
			}
		}

		advancedParts = list.toArray(new AdvancedTesslocatorPart[0]);
		return advancedParts;
	}

	public AdvancedTesslocatorPart[] get(TessNetKey key)
	{
		if (keyMap == null)
		{
			keyMap = new HashMap<>();
		}

		AdvancedTesslocatorPart[] array = keyMap.get(key);

		if (array == null)
		{
			ArrayList<AdvancedTesslocatorPart> list = new ArrayList<>();

			for (AdvancedTesslocatorPart part : getAll())
			{
				if (key.equals(part.getKey()))
				{
					list.add(part);
				}
			}

			array = list.toArray(new AdvancedTesslocatorPart[0]);
			keyMap.put(key, array);
		}

		return array;
	}
}