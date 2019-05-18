package com.latmod.mods.tesslocator.gui;

import com.latmod.mods.tesslocator.Tesslocator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		addButton(new ButtonFakeSlot(0, guiLeft + 77, guiTop + 16, 22, 22, I18n.format("tesslocator.gui.change_mode"))
		{
			@Override
			public void draw()
			{
			}
		});

		addButton(new ButtonFilter(1, guiLeft + 8, guiTop + 19, I18n.format("tesslocator.gui.input_filter"))
		{
			@Override
			public ItemStack getStack()
			{
				return container.part.mode == 1 ? ItemStack.EMPTY : container.part.inputFilter;
			}
		});

		addButton(new ButtonFilter(2, guiLeft + 44, guiTop + 19, I18n.format("tesslocator.gui.output_filter"))
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

		List<String> list = new ArrayList<>();

		for (GuiButton button : buttonList)
		{
			if (button.isMouseOver() && button instanceof ButtonFakeSlot)
			{
				if (button instanceof ButtonFilter)
				{
					ItemStack stack = ((ButtonFilter) button).getStack();

					if (!stack.isEmpty())
					{
						renderToolTip(stack, mouseX, mouseY);
						continue;
					}
				}

				list.add(((ButtonFakeSlot) button).displayString);

				if (button.id == 0)
				{
					String s;

					switch (container.part.mode)
					{
						case 0:
							s = TextFormatting.BLUE + I18n.format("tesslocator.gui.mode.input");
							break;
						case 1:
							s = TextFormatting.GOLD + I18n.format("tesslocator.gui.mode.output");
							break;
						default:
							s = TextFormatting.YELLOW + I18n.format("tesslocator.gui.mode.io");
					}

					list.add(TextFormatting.GRAY + I18n.format("tesslocator.gui.mode", s));
				}
			}
		}

		Slot slot = getSlotUnderMouse();

		if (slot != null && !slot.getHasStack())
		{
			if (slot.slotNumber == 0)
			{
				list.add(I18n.format("tesslocator.gui.speed_boost"));
			}
			else if (slot.slotNumber == 1)
			{
				list.add(I18n.format("tesslocator.gui.stack_boost"));
			}
		}


		drawHoveringText(list, mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + 80, guiTop + 19, 177, container.part.mode * 17, 16, 16);

		if (container.part.other.getStackInSlot(0).isEmpty())
		{
			drawTexturedModalRect(guiLeft + 116, guiTop + 19, 211, 0, 16, 16);
		}

		if (container.part.other.getStackInSlot(1).isEmpty())
		{
			drawTexturedModalRect(guiLeft + 152, guiTop + 19, 211, 17, 16, 16);
		}

		if (container.part.mode == 0)
		{
			drawTexturedModalRect(guiLeft + 43, guiTop + 18, 211, 34, 18, 18);
		}
		else if (container.part.outputFilter.isEmpty())
		{
			drawTexturedModalRect(guiLeft + 44, guiTop + 19, 228, 17, 16, 16);
		}

		if (container.part.mode == 1)
		{
			drawTexturedModalRect(guiLeft + 7, guiTop + 18, 211, 34, 18, 18);
		}
		else if (container.part.inputFilter.isEmpty())
		{
			drawTexturedModalRect(guiLeft + 8, guiTop + 19, 228, 0, 16, 16);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = I18n.format("item.tesslocator.basic_item_tesslocator.name");
		fontRenderer.drawString(s, (width - fontRenderer.getStringWidth(s)) / 2 - guiLeft, 6, 4210752);
		fontRenderer.drawString(I18n.format("container.inventory"), 7, 40, 4210752);
	}
}