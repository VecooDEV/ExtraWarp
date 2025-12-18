package com.vecoo.extrawarp.util;

import java.util.HashSet;
import java.util.Set;

public class PermissionNodes {
    public static final Set<String> PERMISSION_LIST_MODIFY = new HashSet<>();

    public static String
            WARP_COMMAND = "minecraft.command.warp",
            WARP_SET_COMMAND = "minecraft.command.warp.set",
            PRIVATE_WARP_COMMAND = "minecraft.command.warp.pset",
            WARP_DELETE_COMMAND = "minecraft.command.warp.delete",
            WARP_PRIVATE_COMMAND = "minecraft.command.warp.private",
            WARP_INVITE_COMMAND = "minecraft.command.warp.invite",
            WARP_UNINVITE_COMMAND = "minecraft.command.warp.uninvite",
            WARP_BLACKLIST_COMMAND = "minecraft.command.warp.blacklist",
            WARP_PUBLIC_COMMAND = "minecraft.command.warp.public",
            WARP_RENAME_COMMAND = "minecraft.command.warp.rename",
            WARP_WELCOME_COMMAND = "minecraft.command.warp.welcome",
            WARP_RELOAD_COMMAND = "minecraft.command.warp.reload",
            WARP_ASSETS_COMMAND = "minecraft.command.warp.assets",
            WARP_ASSETS_PLAYER_COMMAND = "minecraft.command.warp.assets.player",
            WARP_TOP_COMMAND = "minecraft.command.warp.top",
            WARP_INFO_COMMAND = "minecraft.command.warp.info",
            WARP_UPDATE_COMMAND = "minecraft.command.warp.update",
            WARP_BYPASS = "extrawarp.bypass";
}
