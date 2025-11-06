package com.vecoo.extrawarp.util;

import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PermissionNodes {
    public static final Set<PermissionNode<?>> PERMISSION_LIST = new HashSet<>();
    public static final Set<PermissionNode<Boolean>> PERMISSION_LIST_MODIFY = new HashSet<>();

    public static PermissionNode<Boolean> WARP_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp");
    public static PermissionNode<Boolean> WARP_SET_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.set");
    public static PermissionNode<Boolean> PRIVATE_WARP_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.pset");
    public static PermissionNode<Boolean> WARP_DELETE_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.delete");
    public static PermissionNode<Boolean> WARP_PRIVATE_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.private");
    public static PermissionNode<Boolean> WARP_INVITE_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.invite");
    public static PermissionNode<Boolean> WARP_UNINVITE_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.uninvite");
    public static PermissionNode<Boolean> WARP_BLACKLIST_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.blacklist");
    public static PermissionNode<Boolean> WARP_PUBLIC_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.public");
    public static PermissionNode<Boolean> WARP_RENAME_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.rename");
    public static PermissionNode<Boolean> WARP_WELCOME_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.welcome");
    public static PermissionNode<Boolean> WARP_RELOAD_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.reload");
    public static PermissionNode<Boolean> WARP_ASSETS_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.assets");
    public static PermissionNode<Boolean> WARP_ASSETS_PLAYER_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.assets.player");
    public static PermissionNode<Boolean> WARP_TOP_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.top");
    public static PermissionNode<Boolean> WARP_INFO_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.info");
    public static PermissionNode<Boolean> WARP_UPDATE_COMMAND = UtilPermission.getPermissionNode("minecraft.command.warp.update");
    public static PermissionNode<Boolean> WARP_BYPASS = UtilPermission.getPermissionNode("extrawarp.bypass");

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

        for (String node : ExtraWarp.getInstance().getConfig().getPermissionListingList()) {
            PermissionNode<Boolean> permissionNode = UtilPermission.getPermissionNode(node);

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
