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
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExtraWarpFactory {
    public static boolean teleportWarp(ServerPlayer player, Warp warp) {
        ServerLevel world = UtilWorld.getWorldByName(warp.getDimensionName());

        if (world == null) {
            return false;
        }

        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(warp.getX(), warp.getY(), warp.getZ());

        if (!player.getAbilities().flying) {
            blockPos = findPosition(blockPos, world);

            if (blockPos == null) {
                return false;
            }
        }

        if (NeoForge.EVENT_BUS.post(new WarpEvent.Teleport(warp, player)).isCanceled()) {
            return false;
        }

        player.teleportTo(world, warp.getX(), blockPos.getY(), warp.getZ(), warp.getYRot(), warp.getXRot());
        player.setDeltaMovement(Vec3.ZERO);
        return true;
    }

    private static BlockPos.MutableBlockPos findPosition(BlockPos.MutableBlockPos blockPos, ServerLevel world) {
        ChunkAccess chunk = world.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FEATURES, true);

        if (chunk == null) {
            return null;
        }

        while (blockPos.getY() > -1) {
            if (!chunk.getBlockState(blockPos).isAir()) {
                break;
            }

            blockPos.move(Direction.DOWN);
        }

        if (blockPos.getY() == -1) {
            return null;
        }

        if (!chunk.getBlockState(blockPos).getCollisionShape(chunk, blockPos).isEmpty()) {
            blockPos.move(Direction.UP);
        }

        return blockPos;
    }

    public static class WarpProvider {
        public static Set<Warp> getWarps() {
            return ExtraWarp.getInstance().getWarpProvider().getStorage();
        }

        public static boolean hasWarpByName(String warpName) {
            for (Warp warp : getWarps()) {
                if (warp.getName().equalsIgnoreCase(warpName)) {
                    return true;
                }
            }

            return false;
        }

        public static Warp getWarpByName(String warpName) {
            for (Warp warp : getWarps()) {
                if (warp.getName().equalsIgnoreCase(warpName)) {
                    return warp;
                }
            }

            return null;
        }

        public static Set<Warp> getWarpsByPlayer(UUID playerUUID) {
            Set<Warp> warps = new HashSet<>();

            for (Warp warp : getWarps()) {
                if (warp.getOwnerUUID().equals(playerUUID)) {
                    warps.add(warp);
                }
            }

            return warps;
        }

        public static boolean addWarp(Warp warp) {
            return ExtraWarp.getInstance().getWarpProvider().addWarp(warp);
        }

        public static boolean removeWarp(Warp warp) {
            return ExtraWarp.getInstance().getWarpProvider().removeWarp(warp);
        }
    }
}