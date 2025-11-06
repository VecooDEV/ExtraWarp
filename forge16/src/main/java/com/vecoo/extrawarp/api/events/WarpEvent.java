package com.vecoo.extrawarp.api.events;

import com.vecoo.extrawarp.storage.warp.Warp;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

public class WarpEvent extends Event {
    private final Warp warp;
    private final ServerPlayerEntity player;

    public WarpEvent(@Nonnull Warp warp, @Nonnull ServerPlayerEntity player) {
        this.warp = warp;
        this.player = player;
    }

    @Nonnull
    public Warp getWarp() {
        return this.warp;
    }

    @Nonnull
    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    @Cancelable
    public static class Teleport extends WarpEvent {
        public Teleport(@Nonnull Warp warp, @Nonnull ServerPlayerEntity player) {
            super(warp, player);
        }
    }
}
