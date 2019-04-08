package com.latmod.mods.tesslocator;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = Tesslocator.MOD_ID)
@Config(modid = Tesslocator.MOD_ID, category = "")
public class TesslocatorConfig
{
	@Config.LangKey("stat.generalButton")
	public static final General general = new General();

	public static class General
	{
		@Config.Comment("Spawns particles when items are transferred.")
		public int boost_max = 16;

		public int boost_multiplier = 4;

		public int boost_starting = 64;
	}

	public static void sync()
	{
		ConfigManager.sync(Tesslocator.MOD_ID, Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(Tesslocator.MOD_ID))
		{
			sync();
		}
	}
}