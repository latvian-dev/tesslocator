package com.latmod.mods.tesslocator.item;

import com.latmod.mods.tesslocator.Tesslocator;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
@GameRegistry.ObjectHolder(Tesslocator.MOD_ID)
public class TesslocatorItems
{
	public static final Item BASIC_ITEM_TESSLOCATOR = Items.AIR;
	public static final Item BASIC_FLUID_TESSLOCATOR = Items.AIR;
	public static final Item BASIC_ENERGY_TESSLOCATOR = Items.AIR;
	public static final Item ADVANCED_ITEM_TESSLOCATOR = Items.AIR;
	public static final Item ADVANCED_FLUID_TESSLOCATOR = Items.AIR;
	public static final Item ADVANCED_ENERGY_TESSLOCATOR = Items.AIR;
}