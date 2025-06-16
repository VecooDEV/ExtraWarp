package com.vecoo.extrawarp.util;

import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.api.factory.ExtraWarpFactory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class Utils {
    public static int maxCountWarp(PlayerEntity player) {
        return UtilPermission.maxValue(ExtraWarp.getInstance().getConfig().getBaseCountWarp(), player, ExtraWarp.getInstance().getConfig().getPermissionListingList());
    }

    public static UUID generateUUID() {
        UUID uuid;

        do {
            uuid = UUID.randomUUID();
        } while (ExtraWarpFactory.WarpProvider.hasWarp(uuid));

        return uuid;
    }
}