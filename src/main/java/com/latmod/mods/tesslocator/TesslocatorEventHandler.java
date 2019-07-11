package com.latmod.mods.tesslocator;

import com.latmod.mods.tesslocator.block.BlockTesslocator;
import com.latmod.mods.tesslocator.block.TileTesslocator;
import com.latmod.mods.tesslocator.block.part.EnumPartType;
import com.latmod.mods.tesslocator.data.TessNet;
import com.latmod.mods.tesslocator.item.AdvancedTesslocatorColorRecipe;
import com.latmod.mods.tesslocator.item.ItemTesslocator;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = Tesslocator.MOD_ID)
public class TesslocatorEventHandler
{
	private static Item withName(Item item, String id)
	{
		item.setCreativeTab(CreativeTabs.REDSTONE);
		item.setTranslationKey("tesslocator." + id);
		item.setRegistryName(id);
		return item;
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(new BlockTesslocator().setRegistryName("tesslocator"));
		GameRegistry.registerTileEntity(TileTesslocator.class, new ResourceLocation(Tesslocator.MOD_ID, "tesslocator"));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> r = event.getRegistry();
		r.register(withName(new ItemTesslocator(EnumPartType.BASIC_ITEM), "basic_item_tesslocator"));
		r.register(withName(new ItemTesslocator(EnumPartType.BASIC_FLUID), "basic_fluid_tesslocator"));
		r.register(withName(new ItemTesslocator(EnumPartType.BASIC_ENERGY), "basic_energy_tesslocator"));
		r.register(withName(new ItemTesslocator(EnumPartType.ADVANCED_ITEM).setDisabled(), "advanced_item_tesslocator"));
		r.register(withName(new ItemTesslocator(EnumPartType.ADVANCED_FLUID).setDisabled(), "advanced_fluid_tesslocator"));
		r.register(withName(new ItemTesslocator(EnumPartType.ADVANCED_ENERGY), "advanced_energy_tesslocator"));
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{
		event.getRegistry().register(new AdvancedTesslocatorColorRecipe().setRegistryName("advanced_tesslocator_color"));
	}

	@SubscribeEvent
	public static void worldLoaded(WorldEvent.Load event)
	{
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			TessNet.SERVER = new TessNet();
			File file = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "data/tesslocators.nbt");

			if (file.exists())
			{
				try
				{
					TessNet.SERVER.read(CompressedStreamTools.readCompressed(new FileInputStream(file)));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent
	public static void worldUnloaded(WorldEvent.Unload event)
	{
		if (TessNet.SERVER != null && !event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			TessNet.SERVER = null;
		}
	}

	@SubscribeEvent
	public static void worldSaved(WorldEvent.Save event)
	{
		if (TessNet.SERVER != null && TessNet.SERVER.isDirty && !event.getWorld().isRemote)
		{
			try
			{
				File file = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "data/tesslocators.nbt");

				if (!file.exists())
				{
					if (!file.createNewFile())
					{
						return;
					}
				}

				NBTTagCompound nbt = new NBTTagCompound();
				TessNet.SERVER.write(nbt);
				CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
				TessNet.SERVER.isDirty = false;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}