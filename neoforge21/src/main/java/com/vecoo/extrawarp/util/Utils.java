package com.vecoo.extrawarp.util;

import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class Utils {
    public static int maxCountWarp(@NotNull ServerPlayer player) {
        return UtilPermission.maxValue(ExtraWarp.getInstance().getConfig().getBaseCountWarp(), player, PermissionNodes.PERMISSION_LIST_MODIFY);
    }
}