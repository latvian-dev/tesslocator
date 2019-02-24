package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.item.TesslocatorItems;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum EnumPartType
{
	BASIC_ITEM("basic_item", BasicItemTesslocatorPart::new, () -> TesslocatorItems.BASIC_ITEM_TESSLOCATOR, false),
	BASIC_FLUID("basic_fluid", BasicFluidTesslocatorPart::new, () -> TesslocatorItems.BASIC_FLUID_TESSLOCATOR, false),
	BASIC_ENERGY("basic_energy", BasicEnergyTesslocatorPart::new, () -> TesslocatorItems.BASIC_ENERGY_TESSLOCATOR, false),
	ADVANCED_ITEM("advanced_item", AdvancedItemTesslocatorPart::new, () -> TesslocatorItems.ADVANCED_ITEM_TESSLOCATOR, true),
	ADVANCED_FLUID("advanced_fluid", AdvancedFluidTesslocatorPart::new, () -> TesslocatorItems.ADVANCED_FLUID_TESSLOCATOR, true),
	ADVANCED_ENERGY("advanced_energy", AdvancedEnergyTesslocatorPart::new, () -> TesslocatorItems.ADVANCED_ENERGY_TESSLOCATOR, true);

	public static final EnumPartType[] VALUES = values();

	@Nullable
	public static EnumPartType byID(String id)
	{
		for (EnumPartType type : VALUES)
		{
			if (type.id.equals(id))
			{
				return type;
			}
		}

		return null;
	}

	public final String id;
	public final PartProvider provider;
	public final Supplier<Item> item;
	public final boolean isAdvanced;

	EnumPartType(String i, PartProvider p, Supplier<Item> it, boolean a)
	{
		id = i;
		provider = p;
		item = it;
		isAdvanced = a;
	}
}