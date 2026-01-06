package com.vecoo.extrawarp.util;

import com.google.common.collect.Sets;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.ExtraWarp;
import lombok.val;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

public class Utils {
    public static int maxCountWarp(@Nonnull PlayerEntity player) {
        val serverConfig = ExtraWarp.getInstance().getServerConfig();

        return UtilPermission.maxValue(serverConfig.getBaseCountWarp(), player, serverConfig.getPermissionList());
    }

    public static boolean isBlockedNameWarp(@Nonnull String warpName) {
        val blockedNamesWarp = Sets.newHashSet("set", "pset", "delete", "private", "invite", "uninvite",
                "blacklist", "public", "rename", "welcome", "help", "reload", "assets", "info", "update");

        blockedNamesWarp.addAll(ExtraWarp.getInstance().getServerConfig().getBlockedNamesWarp());

        return blockedNamesWarp.contains(warpName.toLowerCase());
    }
}