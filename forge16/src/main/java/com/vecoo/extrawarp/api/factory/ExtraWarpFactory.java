package com.vecoo.extrawarp.api.factory;

import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.api.events.WarpEvent;
import com.vecoo.extrawarp.storage.warp.Warp;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExtraWarpFactory {
    public static boolean teleportWarp(ServerPlayerEntity player, Warp warp) {
        ServerWorld world = UtilWorld.getWorldByName(warp.getDimensionName());

        if (world == null) {
            return false;
        }

        BlockPos.Mutable blockPos = new BlockPos.Mutable(warp.getX(), warp.getY(), warp.getZ());

        if (!player.abilities.flying) {
            blockPos = findPosition(blockPos, world);

            if (blockPos == null) {
                return false;
            }
        }

        if (MinecraftForge.EVENT_BUS.post(new WarpEvent.Teleport(warp, player))) {
            return false;
        }

        player.teleportTo(world, warp.getX(), blockPos.getY(), warp.getZ(), warp.getYRot(), warp.getXRot());
        player.setDeltaMovement(Vector3d.ZERO);
        return true;
    }

    private static BlockPos.Mutable findPosition(BlockPos.Mutable blockPos, ServerWorld world) {
        IChunk chunk = world.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FEATURES, true);

        if (chunk == null) {
            return null;
        }

        while (blockPos.getY() > -1) {
            if (!chunk.getBlockState(blockPos).is(Blocks.AIR)) {
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
            return ExtraWarp.getInstance().getWarpProvider().getWarps();
        }

        public static Warp getWarp(UUID warpUUID) {
            for (Warp warp : getWarps()) {
                if (warp.getUUID().equals(warpUUID)) {
                    return warp;
                }
            }

            return null;
        }

        public static boolean hasWarp(UUID warpUUID) {
            for (Warp warp : getWarps()) {
                if (warp.getUUID().equals(warpUUID)) {
                    return true;
                }
            }

            return false;
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