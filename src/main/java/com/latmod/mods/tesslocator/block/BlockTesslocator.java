package com.latmod.mods.tesslocator.block;

import com.latmod.mods.tesslocator.Tesslocator;
import com.latmod.mods.tesslocator.block.part.AdvancedTesslocatorPart;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class BlockTesslocator extends Block
{
	@GameRegistry.ObjectHolder(Tesslocator.MOD_ID + ":tesslocator")
	public static BlockTesslocator INSTANCE;

	public static final PropertyInteger CON_D = PropertyInteger.create("d", 0, 2);
	public static final PropertyInteger CON_U = PropertyInteger.create("u", 0, 2);
	public static final PropertyInteger CON_N = PropertyInteger.create("n", 0, 2);
	public static final PropertyInteger CON_S = PropertyInteger.create("s", 0, 2);
	public static final PropertyInteger CON_W = PropertyInteger.create("w", 0, 2);
	public static final PropertyInteger CON_E = PropertyInteger.create("e", 0, 2);
	public static final PropertyInteger[] CON = {CON_D, CON_U, CON_N, CON_S, CON_W, CON_E};

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
		setDefaultState(blockState.getBaseState()
				.withProperty(CON_D, 0)
				.withProperty(CON_U, 0)
				.withProperty(CON_N, 0)
				.withProperty(CON_S, 0)
				.withProperty(CON_W, 0)
				.withProperty(CON_E, 0)
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
		return new BlockStateContainer(this, CON);
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
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
	@Deprecated
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	@Nullable
	@Deprecated
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	private boolean hasParts(TileTesslocator tesslocator)
	{
		for (int i = 0; i < 6; i++)
		{
			if (tesslocator.parts[i] != null)
			{
				return true;
			}
		}

		return false;
	}

	@Override
	@Nullable
	@Deprecated
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof TileTesslocator))
		{
			return super.collisionRayTrace(state, world, pos, start, end);
		}

		TileTesslocator tile = (TileTesslocator) tileEntity;

		if (!hasParts(tile))
		{
			return super.collisionRayTrace(state, world, pos, start, end);
		}

		Vec3d start1 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		Vec3d end1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		RayTraceResult ray1 = null;
		double dist = Double.POSITIVE_INFINITY;

		for (int i = 0; i < BOXES.length; i++)
		{
			if (tile.parts[i] == null)
			{
				continue;
			}

			RayTraceResult ray = BOXES[i].calculateIntercept(start1, end1);

			if (ray != null)
			{
				double dist1 = ray.hitVec.squareDistanceTo(start1);

				if (dist >= dist1)
				{
					dist = dist1;
					ray1 = ray;
					ray1.subHit = i;
				}
			}
		}

		if (ray1 != null)
		{
			RayTraceResult ray2 = new RayTraceResult(ray1.hitVec.add(pos.getX(), pos.getY(), pos.getZ()), ray1.sideHit, pos);
			ray2.subHit = ray1.subHit;
			return ray2;
		}

		return null;
	}

	@Override
	@Deprecated
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
	{
		RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;

		if (ray != null && ray.subHit >= 0 && ray.subHit < BOXES.length)
		{
			return BOXES[ray.subHit].offset(pos);
		}

		return super.getSelectedBoundingBox(state, worldIn, pos);
	}

	@Override
	@Deprecated
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileTesslocator)
		{
			for (int i = 0; i < 6; i++)
			{
				if (((TileTesslocator) tileEntity).parts[i] != null)
				{
					state = state.withProperty(CON[i], ((TileTesslocator) tileEntity).parts[i] instanceof AdvancedTesslocatorPart ? 2 : 1);
				}
			}
		}

		return state;
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
		RayTraceResult ray = collisionRayTrace(state, world, pos, start, end);
		EnumFacing side = ray != null && ray.subHit >= 0 && ray.subHit < 6 ? EnumFacing.byIndex(ray.subHit) : null;

		if (side != null && ((TileTesslocator) tileEntity).parts[side.getIndex()] != null)
		{
			((TileTesslocator) tileEntity).parts[side.getIndex()].onRightClick(player, hand);
			return true;
		}

		return false;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Items.AIR;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult ray, World world, BlockPos pos, EntityPlayer player)
	{
		EnumFacing side = ray != null && ray.subHit >= 0 && ray.subHit < 6 ? EnumFacing.byIndex(ray.subHit) : null;

		if (side != null)
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof TileTesslocator)
			{
				TileTesslocator tile = (TileTesslocator) tileEntity;

				if (tile.parts[side.getIndex()] != null)
				{
					ItemStack stack = new ItemStack(tile.parts[side.getIndex()].getType().item.get());

					if (tile.parts[side.getIndex()].getType().isAdvanced)
					{
						stack.setTagInfo("colors", new NBTTagByte((byte) ((AdvancedTesslocatorPart) tile.parts[side.getIndex()]).colors));
					}

					return stack;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
	{
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof TileTesslocator)
		{
			TileTesslocator tile = (TileTesslocator) tileEntity;
			double dist = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
			Vec3d start = player.getPositionEyes(1F);
			Vec3d look = player.getLookVec();
			Vec3d end = start.add(look.x * dist, look.y * dist, look.z * dist);
			RayTraceResult ray = collisionRayTrace(state, world, pos, start, end);
			EnumFacing side = ray != null && ray.subHit >= 0 && ray.subHit < 6 ? EnumFacing.byIndex(ray.subHit) : null;

			if (side != null)
			{
				if (!player.capabilities.isCreativeMode)
				{
					tile.parts[side.getIndex()].drop(world, pos);
				}

				tile.parts[side.getIndex()] = null;

				if (hasParts(tile))
				{
					world.notifyBlockUpdate(pos, state, state, 11);
				}
				else
				{
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
					return true;
				}
			}
		}

		return false;
	}
}