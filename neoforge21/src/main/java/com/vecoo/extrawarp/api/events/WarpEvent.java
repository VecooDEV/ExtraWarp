package com.vecoo.extrawarp.api.events;

import com.vecoo.extrawarp.storage.warp.Warp;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class WarpEvent extends Event {
    private final Warp warp;
    private final ServerPlayer player;

    public WarpEvent(Warp warp, ServerPlayer player) {
        this.warp = warp;
        this.player = player;
    }

    public Warp getWarp() {
        return this.warp;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    public static class Teleport extends WarpEvent implements ICancellableEvent {
        public Teleport(Warp warp, ServerPlayer player) {
            super(warp, player);
        }
    }
}
