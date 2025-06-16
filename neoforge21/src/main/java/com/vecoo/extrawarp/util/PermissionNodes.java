package com.vecoo.extrawarp.util;

import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;
import java.util.List;

public class PermissionNodes {
    public static List<PermissionNode<?>> permissionList = new ArrayList<>();
    public static List<PermissionNode<Boolean>> permissionListModify = new ArrayList<>();

    public static PermissionNode<Boolean> WARP_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_SET_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.set",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> PRIVATE_WARP_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.pset",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_DELETE_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.delete",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_PRIVATE_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.private",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_INVITE_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.invite",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_UNINVITE_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.uninvite",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_BLACKLIST_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.blacklist",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_PUBLIC_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.public",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_RENAME_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.rename",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_WELCOME_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.welcome",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_RELOAD_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.reload",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_ASSETS_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.assets",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_ASSETS_PLAYER_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.assets.player",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_TOP_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.top",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_INFO_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.info",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> WARP_UPDATE_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.warp.update",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);
}
