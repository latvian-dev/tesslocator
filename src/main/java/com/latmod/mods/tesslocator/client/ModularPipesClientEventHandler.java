package com.latmod.mods.tesslocator.client;

import com.latmod.mods.tesslocator.BlockTesslocator;
import com.latmod.mods.tesslocator.Tesslocator;
import com.latmod.mods.tesslocator.TileTesslocator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
public class ModularPipesClientEventHandler
{
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlockTesslocator.INSTANCE), 0, new ModelResourceLocation(BlockTesslocator.INSTANCE.getRegistryName(), "normal"));
	}

	@SubscribeEvent
	public static void registerBlockColors(ColorHandlerEvent.Block event)
	{
		event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
			if (world != null && pos != null && tintIndex >= 0 && tintIndex < 6)
			{
				TileEntity tileEntity = world.getTileEntity(pos);

				if (tileEntity instanceof TileTesslocator)
				{

				}
			}

			return 0xFFFFFFFF;
		}, BlockTesslocator.INSTANCE);
	}
}