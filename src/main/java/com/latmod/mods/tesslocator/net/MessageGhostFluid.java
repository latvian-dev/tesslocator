package com.latmod.mods.tesslocator.net;

import com.latmod.mods.tesslocator.gui.IGhostFluidHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author LatvianModder
 */
public class MessageGhostFluid implements IMessage
{
	public int slot;
	public FluidStack stack;

	public MessageGhostFluid()
	{
	}

	public MessageGhostFluid(int s, FluidStack fs)
	{
		slot = s;
		stack = fs;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		slot = buf.readUnsignedByte();
		stack = FluidStack.loadFluidStackFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(slot);
		ByteBufUtils.writeTag(buf, stack == null ? null : stack.writeToNBT(new NBTTagCompound()));
	}

	public static class Handler implements IMessageHandler<MessageGhostFluid, IMessage>
	{
		@Override
		public IMessage onMessage(MessageGhostFluid message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().player;

			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				if (player.openContainer instanceof IGhostFluidHandler)
				{
					((IGhostFluidHandler) player.openContainer).setGhostFluid(message.slot, message.stack);
				}
			});

			return null;
		}
	}
}