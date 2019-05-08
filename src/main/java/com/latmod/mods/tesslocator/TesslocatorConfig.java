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

	@Config.LangKey("item.tesslocator.basic_item_tesslocator.name")
	public static final BasicItem basic_item = new BasicItem();

	@Config.LangKey("item.tesslocator.basic_fluid_tesslocator.name")
	public static final BasicFluid basic_fluid = new BasicFluid();

	@Config.LangKey("item.tesslocator.basic_energy_tesslocator.name")
	public static final BasicEnergy basic_energy = new BasicEnergy();

	@Config.LangKey("item.tesslocator.advanced_item_tesslocator.name")
	public static final AdvancedItem advanced_item = new AdvancedItem();

	@Config.LangKey("item.tesslocator.advanced_fluid_tesslocator.name")
	public static final AdvancedFluid advanced_fluid = new AdvancedFluid();

	@Config.LangKey("item.tesslocator.advanced_energy_tesslocator.name")
	public static final AdvancedEnergy advanced_energy = new AdvancedEnergy();

	public static class General
	{
		@Config.Comment("Spawns particles on transfer.")
		public boolean particles = true;
	}

	public static class BasicItem
	{
		@Config.RangeInt(min = 0, max = 64)
		public int speed_boost_max = 8;

		@Config.RangeDouble(min = 1)
		public double speed_boost_multiplier = 2;

		@Config.RangeInt(min = 1)
		public int speed_boost_starting = 16;

		@Config.RangeInt(min = 0, max = 16)
		public int stack_boost_max = 8;
	}

	public static class BasicFluid
	{
	}

	public static class BasicEnergy
	{
	}

	public static class AdvancedItem
	{
	}

	public static class AdvancedFluid
	{
	}

	public static class AdvancedEnergy
	{
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