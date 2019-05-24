package com.latmod.mods.tesslocator.net;

import com.latmod.mods.tesslocator.gui.IGhostItemHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author LatvianModder
 */
public class MessageGhostItem implements IMessage
{
	public int slot;
	public ItemStack stack;

	public MessageGhostItem()
	{
	}

	public MessageGhostItem(int s, ItemStack is)
	{
		slot = s;
		stack = is;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		slot = buf.readUnsignedByte();
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(slot);
		ByteBufUtils.writeItemStack(buf, stack);
	}

	public static class Handler implements IMessageHandler<MessageGhostItem, IMessage>
	{
		@Override
		public IMessage onMessage(MessageGhostItem message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().player;

			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				if (player.openContainer instanceof IGhostItemHandler)
				{
					((IGhostItemHandler) player.openContainer).setGhostItem(message.slot, message.stack);
				}
			});

			return null;
		}
	}
}