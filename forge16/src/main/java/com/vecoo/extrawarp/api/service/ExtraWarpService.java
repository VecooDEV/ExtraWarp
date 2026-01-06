package com.vecoo.extrawarp.api.service;

import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.api.events.WarpEvent;
import com.vecoo.extrawarp.service.Warp;
import lombok.val;
import lombok.var;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExtraWarpService {
    @Nonnull
    public static List<Warp> getWarps() {
        return ExtraWarp.getInstance().getWarpService().getWarps();
    }

    public static boolean hasWarpByName(@Nonnull String warpName) {
        return findWarpByName(warpName) != null;
    }

    public static void addWarp(@Nonnull Warp warp) {
        ExtraWarp.getInstance().getWarpService().addWarp(warp);
    }

    public static boolean removeWarp(@Nonnull Warp warp) {
        return ExtraWarp.getInstance().getWarpService().removeWarp(warp);
    }

    @Nullable
    public static Warp findWarpByName(@Nonnull String warpName) {
        for (Warp warp : getWarps()) {
            if (warp.getName().equalsIgnoreCase(warpName)) {
                return warp;
            }
        }

        return null;
    }

    @Nonnull
    public static List<Warp> getWarpsByPlayer(@Nonnull UUID playerUUID) {
        List<Warp> warps = new ArrayList<>();

        for (Warp warp : getWarps()) {
            if (warp.getOwnerUUID().equals(playerUUID)) {
                warps.add(warp);
            }
        }

        return warps;
    }

    public static boolean teleportWarp(@Nonnull ServerPlayerEntity player, @Nonnull Warp warp) {
        val world = UtilWorld.findWorldByName(warp.getDimensionName());

        if (world == null) {
            return false;
        }

        var blockPos = new BlockPos.Mutable(warp.getX(), warp.getY(), warp.getZ());

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

    @Nullable
    private static BlockPos.Mutable findPosition(@Nonnull BlockPos.Mutable blockPos, @Nonnull ServerWorld world) {
        val chunk = world.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FEATURES, true);

        if (chunk == null) {
            return null;
        }

        while (blockPos.getY() > 1) {
            if (!chunk.getBlockState(blockPos).is(Blocks.AIR)) {
                break;
            }

            blockPos.move(Direction.DOWN);
        }

        if (blockPos.getY() <= 1) {
            return null;
        }

        if (!chunk.getBlockState(blockPos).getCollisionShape(chunk, blockPos).isEmpty()) {
            blockPos.move(Direction.UP);
        }

        return blockPos;
    }
}