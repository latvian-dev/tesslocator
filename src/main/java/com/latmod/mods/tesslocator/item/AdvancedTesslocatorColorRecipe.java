package com.latmod.mods.tesslocator.item;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @author LatvianModder
 */
public class AdvancedTesslocatorColorRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IShapedRecipe
{
	public static final String[] DYE_ORE_NAMES = {
			"dyeWhite",
			"dyeOrange",
			"dyeMagenta",
			"dyeLightBlue",
			"dyeYellow",
			"dyeLime",
			"dyePink",
			"dyeGray",
			"dyeLightGray",
			"dyeCyan",
			"dyePurple",
			"dyeBlue",
			"dyeBrown",
			"dyeGreen",
			"dyeRed",
			"dyeBlack",
	};

	private ItemStack resultItem = ItemStack.EMPTY;

	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		resultItem = ItemStack.EMPTY;
		int[] dyeIds = new int[DYE_ORE_NAMES.length];

		for (int i = 0; i < dyeIds.length; i++)
		{
			dyeIds[i] = OreDictionary.getOreID(DYE_ORE_NAMES[i]);
		}

		for (int slot = inv.getWidth(); slot < inv.getSizeInventory() - inv.getWidth(); slot++)
		{
			ItemStack stack = inv.getStackInSlot(slot);

			if (stack.getItem() instanceof ItemTesslocator && ((ItemTesslocator) stack.getItem()).type.isAdvanced)
			{
				ItemStack stackUp = inv.getStackInSlot(slot - inv.getWidth());
				ItemStack stackDown = inv.getStackInSlot(slot + inv.getWidth());

				if (stackUp.isEmpty() || stackDown.isEmpty())
				{
					continue;
				}

				int colUp = -1;

				for (int ore : OreDictionary.getOreIDs(stackUp))
				{
					for (int col = 0; col < 16; col++)
					{
						if (ore == dyeIds[col])
						{
							colUp = col;
						}
					}
				}

				int colDown = -1;

				for (int ore : OreDictionary.getOreIDs(stackDown))
				{
					for (int col = 0; col < 16; col++)
					{
						if (ore == dyeIds[col])
						{
							colDown = col;
						}
					}
				}

				if (colUp == -1 || colDown == -1)
				{
					continue;
				}

				resultItem = stack.copy();
				resultItem.setTagInfo("colors", new NBTTagByte((byte) (colUp | (colDown << 4))));
				return true;
			}
		}

		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		return resultItem.copy();
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return width >= 1 && height >= 3;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return resultItem;
	}

	@Override
	public int getRecipeWidth()
	{
		return 1;
	}

	@Override
	public int getRecipeHeight()
	{
		return 3;
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}
}