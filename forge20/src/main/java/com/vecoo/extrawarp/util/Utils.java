package com.vecoo.extrawarp.util;

import com.google.common.collect.Sets;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Utils {
    public static int maxCountWarp(@NotNull ServerPlayer player) {
        return UtilPermission.maxValue(ExtraWarp.getInstance().getConfig().getBaseCountWarp(), player, PermissionNodes.PERMISSION_LIST_MODIFY);
    }

    public static boolean isBlockedNameWarp(@NotNull String warpName) {
        Set<String> blockedNamesWarp = Sets.newHashSet("set", "pset", "delete", "private", "invite", "uninvite",
                "blacklist", "public", "rename", "welcome", "help", "reload", "assets", "info", "update");
        blockedNamesWarp.addAll(ExtraWarp.getInstance().getConfig().getBlockedNamesWarp());

        return blockedNamesWarp.contains(warpName.toLowerCase());
    }
}