package com.vecoo.extrawarp.util;

import com.vecoo.extralib.util.PermissionUtil;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PermissionNodes {
    private static final Set<PermissionNode<?>> PERMISSION_LIST = new HashSet<>();
    public static final Set<PermissionNode<Boolean>> PERMISSION_LIST_MODIFY = new HashSet<>();

    public static PermissionNode<Boolean> WARP_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp", true);
    public static PermissionNode<Boolean> WARP_SET_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.set", true);
    public static PermissionNode<Boolean> PRIVATE_WARP_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.pset", true);
    public static PermissionNode<Boolean> WARP_DELETE_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.delete", true);
    public static PermissionNode<Boolean> WARP_PRIVATE_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.private", true);
    public static PermissionNode<Boolean> WARP_INVITE_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.invite", true);
    public static PermissionNode<Boolean> WARP_UNINVITE_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.uninvite", true);
    public static PermissionNode<Boolean> WARP_BLACKLIST_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.blacklist", true);
    public static PermissionNode<Boolean> WARP_PUBLIC_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.public", true);
    public static PermissionNode<Boolean> WARP_RENAME_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.rename", false);
    public static PermissionNode<Boolean> WARP_WELCOME_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.welcome", true);
    public static PermissionNode<Boolean> WARP_RELOAD_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.reload", false);
    public static PermissionNode<Boolean> WARP_ASSETS_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.assets", true);
    public static PermissionNode<Boolean> WARP_ASSETS_PLAYER_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.assets.player", false);
    public static PermissionNode<Boolean> WARP_TOP_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.top", true);
    public static PermissionNode<Boolean> WARP_INFO_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.info", true);
    public static PermissionNode<Boolean> WARP_UPDATE_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.warp.update", true);
    public static PermissionNode<Boolean> WARP_BYPASS = PermissionUtil.getPermissionNode("extrawarp.bypass", false);

    public static void registerPermission(@NotNull PermissionGatherEvent.Nodes event) {
        PERMISSION_LIST.add(WARP_COMMAND);
        PERMISSION_LIST.add(WARP_SET_COMMAND);
        PERMISSION_LIST.add(PRIVATE_WARP_COMMAND);
        PERMISSION_LIST.add(WARP_DELETE_COMMAND);
        PERMISSION_LIST.add(WARP_PRIVATE_COMMAND);
        PERMISSION_LIST.add(WARP_INVITE_COMMAND);
        PERMISSION_LIST.add(WARP_UNINVITE_COMMAND);
        PERMISSION_LIST.add(WARP_BLACKLIST_COMMAND);
        PERMISSION_LIST.add(WARP_PUBLIC_COMMAND);
        PERMISSION_LIST.add(WARP_RENAME_COMMAND);
        PERMISSION_LIST.add(WARP_WELCOME_COMMAND);
        PERMISSION_LIST.add(WARP_RELOAD_COMMAND);
        PERMISSION_LIST.add(WARP_ASSETS_COMMAND);
        PERMISSION_LIST.add(WARP_ASSETS_PLAYER_COMMAND);
        PERMISSION_LIST.add(WARP_TOP_COMMAND);
        PERMISSION_LIST.add(WARP_INFO_COMMAND);
        PERMISSION_LIST.add(WARP_UPDATE_COMMAND);
        PERMISSION_LIST.add(WARP_BYPASS);

        for (String node : ExtraWarp.getInstance().getServerConfig().getPermissionList()) {
            PermissionNode<Boolean> permissionNode = PermissionUtil.getPermissionNode(node, false);

            PERMISSION_LIST.add(permissionNode);
            PERMISSION_LIST_MODIFY.add(permissionNode);
        }

        for (PermissionNode<?> node : PERMISSION_LIST) {
            if (!event.getNodes().contains(node)) {
                event.addNodes(node);
            }
        }
    }
}
