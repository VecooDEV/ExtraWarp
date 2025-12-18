package com.vecoo.extrawarp.api.events;

import com.vecoo.extrawarp.service.Warp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class WarpEvent extends Event {
    @NotNull
    private final Warp warp;
    @NotNull
    private final ServerPlayer player;

    @Cancelable
    public static class Teleport extends WarpEvent {
        public Teleport(@NotNull Warp warp, @NotNull ServerPlayer player) {
            super(warp, player);
        }
    }
}
