package com.latmod.mods.tesslocator.integration;

import com.latmod.mods.tesslocator.gui.GuiBasicItemTesslocator;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

/**
 * @author LatvianModder
 */
@JEIPlugin
public class TesslocatorJEIIntegration implements IModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		registry.addGhostIngredientHandler(GuiBasicItemTesslocator.class, new BasicItemTesslocatorJEI());
	}
}