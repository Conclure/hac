package com.heretere.hac.core.implementation.packets.channel;

import com.heretere.hac.api.HACAPI;
import com.heretere.hac.api.events.types.packets.PacketReferences;
import com.heretere.hac.api.player.HACPlayer;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ChannelInjectorBase {
    private static final String AFTER_KEY = "packet_handler";
    private static final String HANDLER_KEY = "hac_packet_handler";

    private final ExecutorService channelChangeExecutor;

    protected ChannelInjectorBase() {
        this.channelChangeExecutor = Executors.newSingleThreadExecutor();
    }

    public void inject(HACPlayer player) {
        this.remove(player);
        this.channelChangeExecutor.execute(() -> this.getPipeline(player.getBukkitPlayer())
                .addBefore(ChannelInjectorBase.AFTER_KEY, ChannelInjectorBase.HANDLER_KEY, new HACChannelHandler(player)));
    }

    public void remove(HACPlayer player) {
        ChannelPipeline pipeline = this.getPipeline(player.getBukkitPlayer());

        this.channelChangeExecutor.execute(() -> {
            if (pipeline.get(ChannelInjectorBase.HANDLER_KEY) != null) {
                pipeline.remove(ChannelInjectorBase.HANDLER_KEY);
            }
        });
    }

    public void shutdown() {
        this.channelChangeExecutor.shutdown();
    }

    protected abstract ChannelPipeline getPipeline(Player player);

    private static class HACChannelHandler extends ChannelDuplexHandler {
        private final HACPlayer player;

        private HACChannelHandler(HACPlayer player) {
            this.player = player;
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            super.write(ctx, msg, promise);

            try {
                this.handle(msg, false);
            } catch (Exception e) {
                e.printStackTrace(); //TODO: move to logger
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);

            try {
                this.handle(msg, true);
            } catch (Exception e) {
                e.printStackTrace(); //TODO: Move to logger
            }
        }

        private void handle(Object packet, boolean clientSide) {
            PacketReferences.PacketReference<?> reference =
                    clientSide ?
                            HACAPI.getInstance().getPacketReferences().getClientSide().get(packet.getClass()) :
                            HACAPI.getInstance().getPacketReferences().getServerSide().get(packet.getClass());

            if (reference == null) {
                return;
            }

            HACAPI.getInstance().getEventManager().callPacketEvent(
                    this.player,
                    reference.getBuilder().create(this.player, packet)
            );
        }
    }
}
