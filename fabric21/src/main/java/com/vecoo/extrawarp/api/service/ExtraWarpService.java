package com.vecoo.extrawarp.api.service;

import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.service.Warp;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExtraWarpService {
    @NotNull
    public static List<Warp> getWarps() {
        return ExtraWarp.getInstance().getWarpService().getWarps();
    }

    public static boolean hasWarpByName(@NotNull String warpName) {
        return findWarpByName(warpName) != null;
    }

    public static void addWarp(@NotNull Warp warp) {
        ExtraWarp.getInstance().getWarpService().addWarp(warp);
    }

    public static boolean removeWarp(@NotNull Warp warp) {
        return ExtraWarp.getInstance().getWarpService().removeWarp(warp);
    }

    @Nullable
    public static Warp findWarpByName(@NotNull String warpName) {
        for (Warp warp : getWarps()) {
            if (warp.getName().equalsIgnoreCase(warpName)) {
                return warp;
            }
        }

        return null;
    }

    @NotNull
    public static List<Warp> getWarpsByPlayer(@NotNull UUID playerUUID) {
        List<Warp> warps = new ArrayList<>();

        for (Warp warp : getWarps()) {
            if (warp.getOwnerUUID().equals(playerUUID)) {
                warps.add(warp);
            }
        }

        return warps;
    }

    public static boolean teleportWarp(@NotNull ServerPlayer player, @NotNull Warp warp) {
        val level = UtilWorld.findLevelByName(warp.getDimensionName());

        if (level == null) {
            return false;
        }

        var blockPos = new BlockPos.MutableBlockPos(warp.getX(), warp.getY(), warp.getZ());

        if (!player.getAbilities().flying) {
            blockPos = findPosition(blockPos, level);

            if (blockPos == null) {
                return false;
            }
        }

        player.teleportTo(level, warp.getX(), blockPos.getY(), warp.getZ(), warp.getYRot(), warp.getXRot());
        player.setDeltaMovement(Vec3.ZERO);
        return true;
    }

    @Nullable
    private static BlockPos.MutableBlockPos findPosition(@NotNull BlockPos.MutableBlockPos blockPos, @NotNull ServerLevel level) {
        val chunk = level.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FEATURES, true);

        if (chunk == null) {
            return null;
        }

        while (blockPos.getY() > level.getMinBuildHeight()) {
            if (!chunk.getBlockState(blockPos).isAir()) {
                break;
            }

            blockPos.move(Direction.DOWN);
        }

        if (blockPos.getY() < level.getMinBuildHeight()) {
            return null;
        }

        if (!chunk.getBlockState(blockPos).getCollisionShape(chunk, blockPos).isEmpty()) {
            blockPos.move(Direction.UP);
        }

        return blockPos;
    }
}