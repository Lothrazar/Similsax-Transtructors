package vorquel.mod.similsaxtranstructors;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import static vorquel.mod.similsaxtranstructors.Config.*;

public class ServerConfig {

    private static SimpleNetworkWrapper network;

    public static void init() {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("Config");
        network.registerMessage(Handler.class, Message.class, 0, Side.CLIENT);
    }

    @SubscribeEvent
    public void sendClientInfo(PlayerEvent.PlayerLoggedInEvent event) {
        network.sendTo(new Message(basicUses, advancedUses, basicRange, advancedRange), (EntityPlayerMP) event.player);
    }

    public static class Message implements IMessage {

        public int basicUses;
        public int advancedUses;
        public int basicRange;
        public int advancedRange;

        public Message() {}

        public Message(int basicUses, int advancedUses, int basicRange, int advancedRange) {
            this.basicUses = basicUses;
            this.advancedUses = advancedUses;
            this.basicRange = basicRange;
            this.advancedRange = advancedRange;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            basicUses = buf.readInt();
            advancedUses = buf.readInt();
            basicRange = buf.readInt();
            advancedRange = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(basicUses);
            buf.writeInt(advancedUses);
            buf.writeInt(basicRange);
            buf.writeInt(advancedRange);
        }
    }

    public static class Handler implements IMessageHandler<Message, IMessage> {

        @Override
        public IMessage onMessage(Message message, MessageContext ctx) {
            ItemSimilsaxTranstructor item = SimilsaxTranstructors.itemSimilsaxTranstructor;
            item.basicUses = message.basicUses;
            item.advancedUses = message.advancedUses;
            item.basicRange = message.basicRange;
            item.advancedRange = message.advancedRange;
            return null;
        }
    }
}
