package com.latmod.mods.tesslocator.integration;

import com.latmod.mods.tesslocator.gui.ContainerBasicFluidTesslocator;
import com.latmod.mods.tesslocator.gui.GuiBasicFluidTesslocator;
import com.latmod.mods.tesslocator.net.MessageGhostFluid;
import com.latmod.mods.tesslocator.net.TesslocatorNetHandler;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BasicFluidTesslocatorJEI implements IGhostIngredientHandler<GuiBasicFluidTesslocator>
{
	@Nullable
	private static FluidStack get(Object ingredient)
	{
		if (ingredient instanceof FluidStack)
		{
			return (FluidStack) ingredient;
		}
		else if (ingredient instanceof ItemStack)
		{
			return ContainerBasicFluidTesslocator.getFromItem((ItemStack) ingredient);
		}

		return null;
	}

	@Override
	public <I> List<Target<I>> getTargets(GuiBasicFluidTesslocator gui, I ingredient, boolean doStart)
	{
		if (get(ingredient) != null)
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
					FluidStack fs = get(ingredient);
					gui.container.setGhostFluid(0, fs);
					TesslocatorNetHandler.NET.sendToServer(new MessageGhostFluid(0, fs));
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
					FluidStack fs = get(ingredient);
					gui.container.setGhostFluid(1, fs);
					TesslocatorNetHandler.NET.sendToServer(new MessageGhostFluid(1, fs));
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