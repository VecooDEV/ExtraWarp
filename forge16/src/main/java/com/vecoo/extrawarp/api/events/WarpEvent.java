package com.vecoo.extrawarp.api.events;

import com.vecoo.extrawarp.service.Warp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

@Getter
@AllArgsConstructor
public class WarpEvent extends Event {
    @Nonnull
    private final Warp warp;
    @Nonnull
    private final ServerPlayerEntity player;

    @Cancelable
    public static class Teleport extends WarpEvent {
        public Teleport(@Nonnull Warp warp, @Nonnull ServerPlayerEntity player) {
            super(warp, player);
        }
    }
}
