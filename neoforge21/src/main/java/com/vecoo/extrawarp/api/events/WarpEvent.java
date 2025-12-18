package com.vecoo.extrawarp.api.events;

import com.vecoo.extrawarp.service.Warp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public abstract class WarpEvent extends Event {
    @NotNull
    private final Warp warp;
    @NotNull
    private final ServerPlayer player;

    public static class Teleport extends WarpEvent implements ICancellableEvent {
        public Teleport(@NotNull Warp warp, @NotNull ServerPlayer player) {
            super(warp, player);
        }
    }
}
