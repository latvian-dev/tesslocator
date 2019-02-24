package com.latmod.mods.tesslocator.block;

import com.latmod.mods.tesslocator.block.part.AdvancedTesslocatorPart;
import com.latmod.mods.tesslocator.block.part.TesslocatorPart;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum TessNet
{
	INSTANCE;

	private final Map<TessNetKey, List<AdvancedTesslocatorPart>> map = new HashMap<>();
	private boolean refresh = true;

	public void refresh()
	{
		refresh = true;
	}

	public void update()
	{
		if (refresh)
		{
			map.clear();
			refresh = false;

			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			if (server != null)
			{
				for (World world : server.worlds)
				{
					if (world != null)
					{
						for (TileEntity tileEntity : world.loadedTileEntityList)
						{
							if (tileEntity instanceof TileTesslocator)
							{
								for (TesslocatorPart part : ((TileTesslocator) tileEntity).parts)
								{
									if (part instanceof AdvancedTesslocatorPart)
									{
										TessNetKey key = new TessNetKey((AdvancedTesslocatorPart) part);
										List<AdvancedTesslocatorPart> list = map.get(key);

										if (list == null)
										{
											list = new ArrayList<>();
											map.put(key, list);
										}

										list.add((AdvancedTesslocatorPart) part);
									}
								}
							}
						}
					}
				}
			}

			//System.out.println(map);
		}
	}

	public List<AdvancedTesslocatorPart> getAllParts(TessNetKey key)
	{
		List<AdvancedTesslocatorPart> list = map.get(key);
		return list == null ? Collections.emptyList() : list;
	}
}