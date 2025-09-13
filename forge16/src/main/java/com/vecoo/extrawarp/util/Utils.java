package com.vecoo.extrawarp.util;

import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.entity.player.PlayerEntity;

public class Utils {
    public static int maxCountWarp(PlayerEntity player) {
        return UtilPermission.maxValue(ExtraWarp.getInstance().getConfig().getBaseCountWarp(), player, ExtraWarp.getInstance().getConfig().getPermissionListingList());
    }
}