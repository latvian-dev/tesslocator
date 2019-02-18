package com.latmod.mods.tesslocator;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
		modid = Tesslocator.MOD_ID,
		name = Tesslocator.MOD_NAME,
		version = Tesslocator.VERSION,
		acceptedMinecraftVersions = "[1.12,)",
		dependencies = "required-after:itemfilters"
)
public class Tesslocator
{
	public static final String MOD_ID = "tesslocator";
	public static final String MOD_NAME = "Modular Pipes";
	public static final String VERSION = "0.0.0.tesslocator";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@Mod.Instance(MOD_ID)
	public static Tesslocator INSTANCE;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		TesslocatorConfig.sync();
	}
}