package com.vecoo.extrawarp.api.events;

import com.vecoo.extrawarp.storage.warp.Warp;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

public class WarpEvent extends Event {
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

    @Cancelable
    public static class Teleport extends WarpEvent {
        public Teleport(@NotNull Warp warp, @NotNull ServerPlayer player) {
            super(warp, player);
        }
    }
}
