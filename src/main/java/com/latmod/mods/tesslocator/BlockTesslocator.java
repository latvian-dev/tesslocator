package com.latmod.mods.tesslocator;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
public class BlockTesslocator extends Block
{
	@GameRegistry.ObjectHolder(Tesslocator.MOD_ID + ":tesslocator")
	public static Block INSTANCE;

	public static final PropertyBool CON_D = PropertyBool.create("d");
	public static final PropertyBool CON_U = PropertyBool.create("u");
	public static final PropertyBool CON_N = PropertyBool.create("n");
	public static final PropertyBool CON_S = PropertyBool.create("s");
	public static final PropertyBool CON_W = PropertyBool.create("w");
	public static final PropertyBool CON_E = PropertyBool.create("e");

	public static final AxisAlignedBB[] BOXES = new AxisAlignedBB[6];

	static
	{
		double h0 = 1D / 16D;
		double h1 = 1D - h0;

		double v0 = 2D / 16D;
		double v1 = 1D - v0;

		BOXES[0] = new AxisAlignedBB(h0, 0D, h0, h1, v0, h1);
		BOXES[1] = new AxisAlignedBB(h0, v1, h0, h1, 1D, h1);
		BOXES[2] = new AxisAlignedBB(h0, h0, 0D, h1, h1, v0);
		BOXES[3] = new AxisAlignedBB(h0, h0, v1, h1, h1, 1D);
		BOXES[4] = new AxisAlignedBB(0D, h0, h0, v0, h1, h1);
		BOXES[5] = new AxisAlignedBB(v1, h0, h0, 1D, h1, h1);
	}

	public BlockTesslocator()
	{
		super(Material.IRON, MapColor.CYAN);
		setHardness(1F);
		setCreativeTab(CreativeTabs.REDSTONE);
		setTranslationKey("tesslocator");

		setDefaultState(blockState.getBaseState()
				.withProperty(CON_D, false)
				.withProperty(CON_U, false)
				.withProperty(CON_N, false)
				.withProperty(CON_S, false)
				.withProperty(CON_W, false)
				.withProperty(CON_E, false)
		);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileTesslocator();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, CON_D, CON_U, CON_N, CON_S, CON_W, CON_E);
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullBlock(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof TileTesslocator))
		{
			return true;
		}

		double dist = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		Vec3d start = player.getPositionEyes(1F);
		Vec3d look = player.getLookVec();
		Vec3d end = start.add(look.x * dist, look.y * dist, look.z * dist);
		RayTraceResult ray = player.world.rayTraceBlocks(start, end, false, true, false);
		EnumFacing side = ray != null && ray.subHit >= 0 && ray.subHit < 6 ? EnumFacing.byIndex(ray.subHit) : null;

		if (side == null && ray != null)
		{
			side = ray.sideHit;
		}

		return side == null;

	}
}