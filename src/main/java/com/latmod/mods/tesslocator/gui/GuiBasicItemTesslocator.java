package com.latmod.mods.tesslocator.gui;

import com.latmod.mods.tesslocator.Tesslocator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * @author LatvianModder
 */
public class GuiBasicItemTesslocator extends GuiContainer
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(Tesslocator.MOD_ID, "textures/gui/basic_item_tesslocator.png");

	public final ContainerBasicItemTesslocator container;

	public GuiBasicItemTesslocator(ContainerBasicItemTesslocator c)
	{
		super(c);
		container = c;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		addButton(new ButtonFilter(0, guiLeft + 17, guiTop + 10));

		addButton(new ButtonFilter(1, guiLeft + 8, guiTop + 28)
		{
			@Override
			public ItemStack getStack()
			{
				return container.part.mode == 1 ? ItemStack.EMPTY : container.part.inputFilter;
			}
		});

		addButton(new ButtonFilter(2, guiLeft + 26, guiTop + 28)
		{
			@Override
			public ItemStack getStack()
			{
				return container.part.mode == 0 ? ItemStack.EMPTY : container.part.outputFilter;
			}
		});
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (container.enchantItem(mc.player, button.id))
		{
			mc.playerController.sendEnchantPacket(container.windowId, button.id);
			return;
		}

		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + 17, guiTop + 10, 177, container.part.mode * 17, 16, 16);

		if (container.part.other.getStackInSlot(0).isEmpty())
		{
			drawTexturedModalRect(guiLeft + 143, guiTop + 10, 211, 0, 16, 16);
		}

		if (container.part.other.getStackInSlot(1).isEmpty())
		{
			drawTexturedModalRect(guiLeft + 143, guiTop + 28, 211, 17, 16, 16);
		}

		if (container.part.mode == 0)
		{
			drawTexturedModalRect(guiLeft + 25, guiTop + 27, 211, 34, 18, 18);
		}
		else if (container.part.outputFilter.isEmpty())
		{
			drawTexturedModalRect(guiLeft + 26, guiTop + 28, 228, 17, 16, 16);
		}

		if (container.part.mode == 1)
		{
			drawTexturedModalRect(guiLeft + 7, guiTop + 27, 211, 34, 18, 18);
		}
		else if (container.part.inputFilter.isEmpty())
		{
			drawTexturedModalRect(guiLeft + 8, guiTop + 28, 228, 0, 16, 16);
		}
	}
}