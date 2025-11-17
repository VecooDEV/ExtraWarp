package com.vecoo.extrawarp.api.factory;

import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.api.events.WarpEvent;
import com.vecoo.extrawarp.storage.warp.Warp;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExtraWarpFactory {
    public static boolean teleportWarp(@NotNull ServerPlayer player, @NotNull Warp warp) {
        ServerLevel level = UtilWorld.getLevelByName(warp.getDimensionName());

        if (level == null) {
            return false;
        }

        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(warp.getX(), warp.getY(), warp.getZ());

        if (!player.getAbilities().flying) {
            blockPos = findPosition(blockPos, level);

            if (blockPos == null) {
                return false;
            }
        }

        if (MinecraftForge.EVENT_BUS.post(new WarpEvent.Teleport(warp, player))) {
            return false;
        }

        player.teleportTo(level, warp.getX(), blockPos.getY(), warp.getZ(), warp.getYRot(), warp.getXRot());
        player.setDeltaMovement(Vec3.ZERO);
        return true;
    }

    @Nullable
    private static BlockPos.MutableBlockPos findPosition(@NotNull BlockPos.MutableBlockPos blockPos, @NotNull ServerLevel level) {
        ChunkAccess chunk = level.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FEATURES, true);

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

    public static class WarpProvider {
        @NotNull
        public static Set<Warp> getWarps() {
            return ExtraWarp.getInstance().getWarpProvider().getStorage();
        }

        @Nullable
        public static Warp getWarpByName(@NotNull String warpName) {
            for (Warp warp : getWarps()) {
                if (warp.getName().equalsIgnoreCase(warpName)) {
                    return warp;
                }
            }

            return null;
        }

        @NotNull
        public static Set<Warp> getWarpsByPlayer(@NotNull UUID playerUUID) {
            Set<Warp> warps = new HashSet<>();

            for (Warp warp : getWarps()) {
                if (warp.getOwnerUUID().equals(playerUUID)) {
                    warps.add(warp);
                }
            }

            return warps;
        }

        public static boolean hasWarpByName(@NotNull String warpName) {
            return getWarpByName(warpName) != null;
        }

        public static boolean addWarp(@NotNull Warp warp) {
            return ExtraWarp.getInstance().getWarpProvider().addWarp(warp);
        }

        public static boolean removeWarp(@NotNull Warp warp) {
            return ExtraWarp.getInstance().getWarpProvider().removeWarp(warp);
        }
    }
}