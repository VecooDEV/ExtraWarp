package com.vecoo.extrawarp.manager;

import com.vecoo.extralib.util.WorldUtil;
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

public class ExtraWarpManager {
    public static boolean teleportWarp(@NotNull ServerPlayer player, @NotNull Warp warp) {
        val level = WorldUtil.findLevelByName(warp.getDimensionName());

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
