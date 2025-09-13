package com.vecoo.extrawarp.util;

import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.server.level.ServerPlayer;

public class Utils {
    public static int maxCountWarp(ServerPlayer player) {
        return UtilPermission.maxValue(ExtraWarp.getInstance().getConfig().getBaseCountWarp(), player, ExtraWarp.getInstance().getConfig().getPermissionListingList());
    }
}