package com.latmod.mods.tesslocator.block.part;

import com.latmod.mods.tesslocator.block.TileTesslocator;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public interface PartProvider
{
	TesslocatorPart createPart(TileTesslocator block, EnumFacing facing);
}