package com.vecoo.extrawarp.api.service;

import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.service.Warp;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExtraWarpService {
    @NotNull
    public static Map<String, Warp> getStorage() {
        return Collections.unmodifiableMap(ExtraWarp.getInstance().getWarpService().getStorage());
    }

    public static boolean hasWarpByName(@NotNull String warpName) {
        return findWarpByName(warpName) != null;
    }

    public static void addWarp(@NotNull Warp warp) {
        ExtraWarp.getInstance().getWarpService().addWarp(warp);
    }

    public static boolean removeWarp(@NotNull String warpName) {
        return ExtraWarp.getInstance().getWarpService().removeWarp(warpName);
    }

    @Nullable
    public static Warp findWarpByName(@NotNull String warpName) {
        return ExtraWarp.getInstance().getWarpService().getStorage(warpName);
    }

    @NotNull
    public static Set<Warp> getWarpsByPlayer(@NotNull UUID playerUUID) {
        val playerWarps = ExtraWarp.getInstance().getWarpService().getPlayerWarpsCache().get(playerUUID);

        return playerWarps != null ? Collections.unmodifiableSet(playerWarps) : Collections.emptySet();
    }

    public static boolean invitePlayer(@NotNull String warpName, @NotNull UUID targetUUID) {
        val added = new AtomicBoolean(false);

        ExtraWarp.getInstance().getWarpService().modifyStorage(warpName, warp -> {
            added.set(warp.addInvitePlayer(targetUUID));
        });

        return added.get();
    }

    public static boolean removeInvitePlayer(@NotNull String warpName, @NotNull UUID targetUUID) {
        val added = new AtomicBoolean(false);

        ExtraWarp.getInstance().getWarpService().modifyStorage(warpName, warp -> {
            added.set(warp.removeInvitePlayer(targetUUID));
        });

        return added.get();
    }

    public static boolean blacklistPlayer(@NotNull String warpName, @NotNull UUID targetUUID) {
        val added = new AtomicBoolean(false);

        ExtraWarp.getInstance().getWarpService().modifyStorage(warpName, warp -> {
            added.set(warp.addBlacklistPlayer(targetUUID));
        });

        return added.get();
    }

    public static boolean removeBlacklistPlayer(@NotNull String warpName, @NotNull UUID targetUUID) {
        val added = new AtomicBoolean(false);

        ExtraWarp.getInstance().getWarpService().modifyStorage(warpName, warp -> {
            added.set(warp.removeBlacklistPlayer(targetUUID));
        });

        return added.get();
    }
}