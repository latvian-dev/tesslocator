package com.latmod.mods.tesslocator.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ButtonFilter extends ButtonFakeSlot
{
	public ButtonFilter(int id, int x, int y)
	{
		super(id, x, y, "");
	}

	public ItemStack getStack()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (visible)
		{
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

			ItemStack stack = getStack();

			if (!stack.isEmpty())
			{
				GlStateManager.color(1F, 1F, 1F, 1F);
				GlStateManager.enableDepth();
				RenderHelper.enableGUIStandardItemLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				RenderItem renderItem = mc.getRenderItem();
				renderItem.renderItemAndEffectIntoGUI(mc.player, stack, x, y);
				renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, null);
				RenderHelper.disableStandardItemLighting();
			}

			if (hovered)
			{
				GlStateManager.color(1F, 1F, 1F, 1F);
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(x, y, x + width, y + height, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}
	}

	@Override
	public void playPressSound(SoundHandler soundHandler)
	{
	}
}