package com.vecoo.extrawarp.api.events;

import com.vecoo.extrawarp.storage.warp.Warp;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class WarpEvent extends Event {
    private final Warp warp;
    private final ServerPlayerEntity player;

    public WarpEvent(Warp warp, ServerPlayerEntity player) {
        this.warp = warp;
        this.player = player;
    }

    public Warp getWarp() {
        return this.warp;
    }

    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    @Cancelable
    public static class Teleport extends WarpEvent {
        public Teleport(Warp warp, ServerPlayerEntity player) {
            super(warp, player);
        }
    }
}
