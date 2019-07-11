package com.latmod.mods.tesslocator.net;

import com.latmod.mods.tesslocator.Tesslocator;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
public class TesslocatorNetHandler
{
	public static SimpleNetworkWrapper NET;

	public static void init()
	{
		NET = new SimpleNetworkWrapper(Tesslocator.MOD_ID);
		NET.registerMessage(new MessageGhostItem.Handler(), MessageGhostItem.class, 0, Side.SERVER);
		NET.registerMessage(new MessageGhostFluid.Handler(), MessageGhostFluid.class, 1, Side.SERVER);
	}
}