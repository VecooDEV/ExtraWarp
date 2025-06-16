package com.vecoo.extrawarp.util;

import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.api.factory.ExtraWarpFactory;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class Utils {
    public static int maxCountWarp(ServerPlayer player) {
        return UtilPermission.maxValue(ExtraWarp.getInstance().getConfig().getBaseCountWarp(), player, PermissionNodes.permissionListModify);
    }

    public static UUID generateUUID() {
        UUID uuid;

        do {
            uuid = UUID.randomUUID();
        } while (ExtraWarpFactory.WarpProvider.hasWarp(uuid));

        return uuid;
    }
}