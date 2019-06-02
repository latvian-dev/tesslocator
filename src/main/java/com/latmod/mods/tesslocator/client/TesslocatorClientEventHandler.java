package com.latmod.mods.tesslocator.client;

import com.latmod.mods.tesslocator.Tesslocator;
import com.latmod.mods.tesslocator.block.BlockTesslocator;
import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.item.TesslocatorItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = Tesslocator.MOD_ID, value = Side.CLIENT)
public class TesslocatorClientEventHandler
{
	private static void registerModel(Item item, String variant)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), variant));
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		registerModel(TesslocatorItems.BASIC_ITEM_TESSLOCATOR, "inventory");
		registerModel(TesslocatorItems.BASIC_FLUID_TESSLOCATOR, "inventory");
		registerModel(TesslocatorItems.BASIC_ENERGY_TESSLOCATOR, "inventory");
		registerModel(TesslocatorItems.ADVANCED_ITEM_TESSLOCATOR, "inventory");
		registerModel(TesslocatorItems.ADVANCED_FLUID_TESSLOCATOR, "inventory");
		registerModel(TesslocatorItems.ADVANCED_ENERGY_TESSLOCATOR, "inventory");
	}

	@SubscribeEvent
	public static void registerBlockColors(ColorHandlerEvent.Block event)
	{
		event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
			if (world != null && pos != null && tintIndex >= 0)
			{
				TileEntity tileEntity = world.getTileEntity(pos);

				if (tileEntity instanceof TileTesslocator)
				{
					TileTesslocator tile = (TileTesslocator) tileEntity;

					if (tile.parts[tintIndex % 6] != null)
					{
						return 0xFF000000 | tile.parts[tintIndex % 6].getColor(tintIndex / 6);
					}
				}
			}

			return 0xFFFFFFFF;
		}, BlockTesslocator.INSTANCE);
	}

	@SubscribeEvent
	public static void registerItemColors(ColorHandlerEvent.Item event)
	{
		event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			int colors = stack.hasTagCompound() ? stack.getTagCompound().getByte("colors") & 0xFF : 0;
			return 0xFF000000 | EnumDyeColor.byMetadata((colors >> (tintIndex * 4)) & 0xF).getColorValue();
		}, TesslocatorItems.ADVANCED_ITEM_TESSLOCATOR, TesslocatorItems.ADVANCED_FLUID_TESSLOCATOR, TesslocatorItems.ADVANCED_ENERGY_TESSLOCATOR);

		event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
			if (world != null && pos != null && tintIndex >= 0)
			{
				TileEntity tileEntity = world.getTileEntity(pos);

				if (tileEntity instanceof TileTesslocator)
				{
					TileTesslocator tile = (TileTesslocator) tileEntity;

					if (tile.parts[tintIndex % 6] != null)
					{
						return 0xFF000000 | tile.parts[tintIndex % 6].getColor(tintIndex / 6);
					}
				}
			}

			return 0xFFFFFFFF;
		});
	}
}