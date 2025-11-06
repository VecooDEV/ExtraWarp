package com.vecoo.extrawarp.util;

import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.config.ServerConfig;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

public class Utils {
    public static int maxCountWarp(@Nonnull PlayerEntity player) {
        ServerConfig config = ExtraWarp.getInstance().getConfig();

        return UtilPermission.maxValue(config.getBaseCountWarp(), player, config.getPermissionListingList());
    }
}