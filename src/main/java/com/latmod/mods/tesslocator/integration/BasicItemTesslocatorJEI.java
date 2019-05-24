package com.latmod.mods.tesslocator.integration;

import com.latmod.mods.tesslocator.gui.GuiBasicItemTesslocator;
import com.latmod.mods.tesslocator.net.MessageGhostItem;
import com.latmod.mods.tesslocator.net.TesslocatorNetHandler;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BasicItemTesslocatorJEI implements IGhostIngredientHandler<GuiBasicItemTesslocator>
{
	@Override
	public <I> List<Target<I>> getTargets(GuiBasicItemTesslocator gui, I ingredient, boolean doStart)
	{
		if (ingredient instanceof ItemStack)
		{
			List<Target<I>> list = new ArrayList<>();

			list.add(new Target<I>()
			{
				@Override
				public Rectangle getArea()
				{
					return new Rectangle(gui.getGuiLeft() + 8, gui.getGuiTop() + 19, 16, 16);
				}

				@Override
				public void accept(I ingredient)
				{
					gui.container.setGhostItem(0, (ItemStack) ingredient);
					TesslocatorNetHandler.NET.sendToServer(new MessageGhostItem(0, (ItemStack) ingredient));
				}
			});

			list.add(new Target<I>()
			{
				@Override
				public Rectangle getArea()
				{
					return new Rectangle(gui.getGuiLeft() + 44, gui.getGuiTop() + 19, 16, 16);
				}

				@Override
				public void accept(I ingredient)
				{
					gui.container.setGhostItem(1, (ItemStack) ingredient);
					TesslocatorNetHandler.NET.sendToServer(new MessageGhostItem(1, (ItemStack) ingredient));
				}
			});

			return list;
		}

		return Collections.emptyList();
	}

	@Override
	public void onComplete()
	{
	}
}