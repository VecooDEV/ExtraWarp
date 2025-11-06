package com.vecoo.extrawarp.api.events;

import com.vecoo.extrawarp.storage.warp.Warp;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class WarpEvent extends Event {
    private final Warp warp;
    private final ServerPlayer player;

    public WarpEvent(@NotNull Warp warp, @NotNull ServerPlayer player) {
        this.warp = warp;
        this.player = player;
    }

    @NotNull
    public Warp getWarp() {
        return this.warp;
    }

    @NotNull
    public ServerPlayer getPlayer() {
        return this.player;
    }

    public static class Teleport extends WarpEvent implements ICancellableEvent {
        public Teleport(@NotNull Warp warp, @NotNull ServerPlayer player) {
            super(warp, player);
        }
    }
}
