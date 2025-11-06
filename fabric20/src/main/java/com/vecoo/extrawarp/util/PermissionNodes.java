package com.vecoo.extrawarp.util;

import java.util.HashSet;
import java.util.Set;

public class PermissionNodes {
    public static final Set<String> PERMISSION_LIST_MODIFY = new HashSet<>();

    public static String WARP_COMMAND = "minecraft.command.warp";
    public static String WARP_SET_COMMAND = "minecraft.command.warp.set";
    public static String PRIVATE_WARP_COMMAND = "minecraft.command.warp.pset";
    public static String WARP_DELETE_COMMAND = "minecraft.command.warp.delete";
    public static String WARP_PRIVATE_COMMAND = "minecraft.command.warp.private";
    public static String WARP_INVITE_COMMAND = "minecraft.command.warp.invite";
    public static String WARP_UNINVITE_COMMAND = "minecraft.command.warp.uninvite";
    public static String WARP_BLACKLIST_COMMAND = "minecraft.command.warp.blacklist";
    public static String WARP_PUBLIC_COMMAND = "minecraft.command.warp.public";
    public static String WARP_RENAME_COMMAND = "minecraft.command.warp.rename";
    public static String WARP_WELCOME_COMMAND = "minecraft.command.warp.welcome";
    public static String WARP_RELOAD_COMMAND = "minecraft.command.warp.reload";
    public static String WARP_ASSETS_COMMAND = "minecraft.command.warp.assets";
    public static String WARP_ASSETS_PLAYER_COMMAND = "minecraft.command.warp.assets.player";
    public static String WARP_TOP_COMMAND = "minecraft.command.warp.top";
    public static String WARP_INFO_COMMAND = "minecraft.command.warp.info";
    public static String WARP_UPDATE_COMMAND = "minecraft.command.warp.update";
    public static String WARP_BYPASS = "extrawarp.bypass";
}
