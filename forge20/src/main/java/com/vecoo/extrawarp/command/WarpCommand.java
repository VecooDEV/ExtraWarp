package com.vecoo.extrawarp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.util.*;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.api.service.ExtraWarpService;
import com.vecoo.extrawarp.manager.ExtraWarpManager;
import com.vecoo.extrawarp.service.Warp;
import com.vecoo.extrawarp.util.PermissionNodes;
import com.vecoo.extrawarp.util.Utils;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.UsernameCache;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarpCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("warp")
                .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_COMMAND))
                .then(Commands.argument("warp", StringArgumentType.string())
                        .executes(e -> executeWarp(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "warp"))))

                .then(Commands.literal("set")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_SET_COMMAND))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(e -> executeSet(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "name")))))

                .then(Commands.literal("pset")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.PRIVATE_WARP_COMMAND))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(e -> executePrivateSet(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "name")))))

                .then(Commands.literal("delete")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_DELETE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeDelete(e.getSource(), StringArgumentType.getString(e, "warp")))))

                .then(Commands.literal("private")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_PRIVATE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (!warp.isLocked() && warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executePrivate(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "warp")))))

                .then(Commands.literal("invite")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_INVITE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("player", StringArgumentType.string())
                                        .suggests(CommandUtil.suggestOnlinePlayers())
                                        .executes(e -> executeInvite(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player"), StringArgumentType.getString(e, "warp"))))))

                .then(Commands.literal("uninvite")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_UNINVITE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("player", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                for (UUID playerUUID : warp.getInvitePlayers()) {
                                                    val playerName = PlayerUtil.getPlayerName(playerUUID);

                                                    if (playerName.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                        builder.suggest(playerName);
                                                    }
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(e -> executeUnInvite(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player"), StringArgumentType.getString(e, "warp"))))))

                .then(Commands.literal("blacklist")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_BLACKLIST_COMMAND))
                        .then(Commands.literal("add")
                                .then(Commands.argument("warp", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(warp.getName());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("player", StringArgumentType.string())
                                                .suggests(CommandUtil.suggestOnlinePlayers())
                                                .executes(e -> executeAddBlacklist(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player"), StringArgumentType.getString(e, "warp"))))))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("warp", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(warp.getName());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("player", StringArgumentType.string())
                                                .suggests((s, builder) -> {
                                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                        for (UUID playerUUID : warp.getBlacklistPlayers()) {
                                                            val playerName = PlayerUtil.getPlayerName(playerUUID);

                                                            if (playerName.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                                builder.suggest(playerName);
                                                            }
                                                        }
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(e -> executeRemoveBlacklist(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player"), StringArgumentType.getString(e, "warp")))))))

                .then(Commands.literal("public")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_PUBLIC_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.isLocked() && warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executePublic(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "warp")))))

                .then(Commands.literal("welcome")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_WELCOME_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeRemoveWelcome(e.getSource(), StringArgumentType.getString(e, "warp")))
                                .then(Commands.argument("message", MessageArgument.message())
                                        .executes(e -> executeSetWelcome(e.getSource(), StringArgumentType.getString(e, "warp"), MessageArgument.getMessage(e, "message"))))))

                .then(Commands.literal("help")
                        .executes(e -> executeHelp(e.getSource())))

                .then(Commands.literal("reload")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_RELOAD_COMMAND))
                        .executes(e -> executeReload(e.getSource())))

                .then(Commands.literal("assets")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_ASSETS_COMMAND))
                        .executes(e -> executeAssets(e.getSource().getPlayerOrException()))

                        .then(Commands.argument("player", StringArgumentType.string())
                                .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_ASSETS_PLAYER_COMMAND))
                                .suggests(CommandUtil.suggestOnlinePlayers())
                                .executes(e -> executeAssetsPlayer(e.getSource(), StringArgumentType.getString(e, "player")))))

                .then(Commands.literal("top")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_TOP_COMMAND))
                        .executes(e -> executeTop(e.getSource())))

                .then(Commands.literal("info")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_INFO_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executeInfo(e.getSource(), StringArgumentType.getString(e, "warp")))))

                .then(Commands.literal("update")
                        .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.WARP_UPDATE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeUpdate(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "warp"))))));
    }

    private static int executeWarp(@NotNull ServerPlayer player, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!isPlayerInvitedWarp(player, warp)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpPrivate()
                    .replace("%warp%", name)));
            return 0;
        }

        if (isPlayerBlacklistedWarp(player, warp)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getBlacklistWarp()
                    .replace("%warp%", name)));
            return 0;
        }

        val level = WorldUtil.findLevelByName(warp.getDimensionName());

        if (level == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotDimension()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (isWarpBeyondWorld(warp, level)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpBorder()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!ExtraWarpManager.teleportWarp(player, warp)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        ExtraWarp.getInstance().getWarpService().modifyStorage(name, modifyWarp -> modifyWarp.addUniquePlayer(player.getUUID()));

        if (warp.getWelcomeText().isEmpty()) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getTeleportWarp()
                    .replace("%warp%", warp.getName())));
        } else {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getAddWelcome() + warp.getWelcomeText()));
        }

        return 1;
    }

    private static int executeSet(@NotNull ServerPlayer player, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val maxWarps = Utils.maxCountWarp(player);

        if (isLimitWarp(player, maxWarps)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarps))));
            return 0;
        }

        if (Utils.isBlockedNameWarp(name)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getInvalidWarpArgument()));
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getServerConfig().getMaxCharactersWarp()) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpMaxCharacters()));
            return 0;
        }

        if (ExtraWarpService.hasWarpByName(name)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)));
            return 0;
        }

        ExtraWarpService.addWarp(new Warp(name, player, false));
        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getSetWarp()
                .replace("%warp%", name)).copy().append(TextUtil.clickableMessageCommand(localeConfig.getSetWarpAdditional(),
                        "/warp private " + name).copy()
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextUtil.formatMessage(localeConfig.getHoverSetToPrivateWarp() + name))))));
        return 1;
    }

    private static int executePrivateSet(@NotNull ServerPlayer player, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val maxWarps = Utils.maxCountWarp(player);

        if (isLimitWarp(player, maxWarps)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarps))));
            return 0;
        }

        if (Utils.isBlockedNameWarp(name)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getInvalidWarpArgument()));
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getServerConfig().getMaxCharactersWarp()) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpMaxCharacters()));
            return 0;
        }

        if (ExtraWarpService.hasWarpByName(name)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)));
            return 0;
        }

        ExtraWarpService.addWarp(new Warp(name, player, true));
        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getSetWarpPrivate()
                .replace("%warp%", name)));
        return 1;
    }

    private static int executeDelete(@NotNull CommandSourceStack source, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!isWarpOwner(source, warp)) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!ExtraWarpService.removeWarp(name)) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)));
            return 0;
        }

        source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpRemoved()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeInfo(@NotNull CommandSourceStack source, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        val playerInviteName = new HashSet<>();
        val playerBlacklistName = new HashSet<>();

        for (UUID playerUUID : warp.getInvitePlayers()) {
            if (UsernameCache.containsUUID(playerUUID)) {
                playerInviteName.add(PlayerUtil.getPlayerName(playerUUID));
            }
        }

        for (UUID playerUUID : warp.getBlacklistPlayers()) {
            if (UsernameCache.containsUUID(playerUUID)) {
                playerBlacklistName.add(PlayerUtil.getPlayerName(playerUUID));
            }
        }

        val player = source.getPlayer();
        boolean hideXYZ = warp.isLocked() && player != null && !PermissionUtil.hasPermission(player, PermissionNodes.WARP_BYPASS)
                && !warp.getOwnerUUID().equals(player.getUUID()) && !warp.getInvitePlayers().contains(player.getUUID());

        source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getInfoWarp()
                .replace("%warp%", warp.getName())
                .replace("%owner%", PlayerUtil.getPlayerName(warp.getOwnerUUID()))
                .replace("%x%", hideXYZ ? "-" : String.valueOf(warp.getX()))
                .replace("%y%", hideXYZ ? "-" : String.valueOf(warp.getY()))
                .replace("%z%", hideXYZ ? "-" : String.valueOf(warp.getZ()))
                .replace("%dimension%", warp.getDimensionName())
                .replace("%invitePlayers%", playerInviteName.toString())
                .replace("%blacklistPlayers%", playerBlacklistName.toString())
                .replace("%count%", String.valueOf(warp.getUniquePlayers().size()))
                .replace("%locked%", warp.isLocked() ? localeConfig.getLocked() : localeConfig.getUnlocked())));
        return 1;
    }

    private static int executeUpdate(@NotNull ServerPlayer player, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        val level = WorldUtil.findLevelByName(warp.getDimensionName());

        if (level == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotDimension()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (isWarpBeyondWorld(warp, level)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpBorder()
                    .replace("%warp%", name)));
            return 0;
        }

        ExtraWarp.getInstance().getWarpService().modifyStorage(name, modifyWarp -> modifyWarp.updatePosition(player));
        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpUpdate()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executePrivate(@NotNull ServerPlayer player, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (warp.isLocked()) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpPrivated()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        ExtraWarp.getInstance().getWarpService().modifyStorage(name, modifyWarp -> modifyWarp.setLocked(true));
        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getPrivateWarp()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executePublic(@NotNull ServerPlayer player, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!warp.isLocked()) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpPubliced()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        ExtraWarp.getInstance().getWarpService().modifyStorage(name, modifyWarp -> modifyWarp.setLocked(false));
        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getPublicWarp()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeInvite(@NotNull ServerPlayer player, @NotNull String target, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        val targetUUID = PlayerUtil.findUUID(target);

        if (targetUUID == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!ExtraWarpService.invitePlayer(name, targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", target)));
            return 0;
        }

        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", target)));

        PlayerUtil.sendMessageUUID(targetUUID, TextUtil.formatMessage(localeConfig.getInvitedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", player.getName().getString())));
        return 1;
    }

    private static int executeUnInvite(@NotNull ServerPlayer player, @NotNull String target, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        val targetUUID = PlayerUtil.findUUID(target);

        if (targetUUID == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!ExtraWarpService.removeInvitePlayer(name, targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", target)));
            return 0;
        }

        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getUnInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", target)));
        return 1;
    }

    private static int executeAddBlacklist(@NotNull ServerPlayer player, @NotNull String target, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        UUID targetUUID = PlayerUtil.findUUID(target);

        if (targetUUID == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!ExtraWarpService.blacklistPlayer(name, targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", target)));
            return 0;
        }

        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getBlacklistAddedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", target)));
        return 1;
    }

    private static int executeRemoveBlacklist(@NotNull ServerPlayer player, @NotNull String target, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        UUID targetUUID = PlayerUtil.findUUID(target);

        if (targetUUID == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID())) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!ExtraWarpService.removeBlacklistPlayer(name, targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", target)));
            return 0;
        }

        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getBlacklistRemovedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", target)));
        return 1;
    }

    private static int executeAssets(@NotNull ServerPlayer player) {
        val warps = ExtraWarpService.getWarpsByPlayer(player.getUUID());

        val publicWarps = warps.stream()
                .filter(warp -> !warp.isLocked())
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        val privateWarps = warps.stream()
                .filter(Warp::isLocked)
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        player.sendSystemMessage(TextUtil.formatMessage(ExtraWarp.getInstance().getLocaleConfig().getWarpAssets()
                .replace("%count%", String.valueOf(warps.size()))
                .replace("%maxCount%", String.valueOf(Utils.maxCountWarp(player)))
                .replace("%publicWarps%", publicWarps)
                .replace("%privateWarps%", privateWarps)));
        return 1;
    }

    private static int executeAssetsPlayer(@NotNull CommandSourceStack source, @NotNull String target) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val targetUUID = PlayerUtil.findUUID(target);

        if (targetUUID == null) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        val warps = ExtraWarpService.getWarpsByPlayer(targetUUID);

        val publicWarps = warps.stream()
                .filter(warp -> !warp.isLocked())
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        val privateWarps = warps.stream()
                .filter(Warp::isLocked)
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpAssetsPlayer()
                .replace("%player%", target)
                .replace("%publicWarps%", publicWarps)
                .replace("%privateWarps%", privateWarps)));
        return 1;
    }

    private static int executeTop(@NotNull CommandSourceStack source) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val topWarps = ExtraWarpService.getStorage().values().stream()
                .filter(warp -> !warp.isLocked())
                .sorted(Comparator.comparingInt((Warp warp) -> warp.getUniquePlayers().size()).reversed())
                .limit(10)
                .toList();

        source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getTopWarpTitle()));

        for (int i = 0; i < topWarps.size(); i++) {
            val warp = topWarps.get(i);

            source.sendSystemMessage(TextUtil.clickableMessageCommand(localeConfig.getTopWarp()
                            .replace("%place%", localeConfig.getPlaces().get(i))
                            .replace("%warp%", warp.getName())
                            .replace("%player%", PlayerUtil.getPlayerName(warp.getOwnerUUID())),
                    "/warp " + warp.getName()).copy().withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    TextUtil.formatMessage(localeConfig.getHoverTopWarp() + warp.getName())))));
        }

        return 1;
    }

    private static int executeSetWelcome(@NotNull CommandSourceStack source, @NotNull String name, Component component) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!isWarpOwner(source, warp)) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        ExtraWarp.getInstance().getWarpService().modifyStorage(name, modifyWarp -> modifyWarp.setWelcomeText(component.getString()));
        source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpSetWelcome()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeRemoveWelcome(@NotNull CommandSourceStack source, @NotNull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!isWarpOwner(source, warp)) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getWelcomeText().isEmpty()) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpWelcomeEmpty()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        ExtraWarp.getInstance().getWarpService().modifyStorage(name, modifyWarp -> modifyWarp.setWelcomeText(null));
        source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getWarpRemoveWelcome()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeHelp(@NotNull CommandSourceStack source) {
        source.sendSystemMessage(TextUtil.formatMessage(ExtraWarp.getInstance().getLocaleConfig().getHelp()));
        return 1;
    }

    private static int executeReload(@NotNull CommandSourceStack source) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();

        try {
            ExtraWarp.getInstance().loadConfig();
        } catch (Exception e) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getErrorReload()));
            ExtraWarp.getLogger().error(e.getMessage());
            return 0;
        }

        source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getReload()));
        return 1;
    }

    private static boolean isPlayerBlacklistedWarp(@NotNull ServerPlayer player, @NotNull Warp warp) {
        return warp.getBlacklistPlayers().contains(player.getUUID()) && !PermissionUtil.hasPermission(player, PermissionNodes.WARP_BYPASS);
    }

    private static boolean isPlayerInvitedWarp(@NotNull ServerPlayer player, @NotNull Warp warp) {
        return !warp.isLocked() || warp.getOwnerUUID().equals(player.getUUID()) || warp.getInvitePlayers().contains(player.getUUID())
                || PermissionUtil.hasPermission(player, PermissionNodes.WARP_BYPASS);
    }

    private static boolean isLimitWarp(@NotNull ServerPlayer player, int maxCount) {
        return ExtraWarpService.getWarpsByPlayer(player.getUUID()).size() >= maxCount
                && !PermissionUtil.hasPermission(player, PermissionNodes.WARP_BYPASS);
    }

    private static boolean isWarpOwner(@NotNull CommandSourceStack source, @NotNull Warp warp) {
        return source.getEntity() == null || warp.getOwnerUUID().equals(source.getEntity().getUUID())
                || PermissionUtil.hasPermission(source, PermissionNodes.WARP_BYPASS);
    }

    private static boolean isWarpOwner(@NotNull ServerPlayer player, @NotNull Warp warp) {
        return warp.getOwnerUUID().equals(player.getUUID()) || PermissionUtil.hasPermission(player, PermissionNodes.WARP_BYPASS);
    }

    private static boolean isWarpBeyondWorld(@NotNull Warp warp, @NotNull Level level) {
        return warp.getX() >= level.getWorldBorder().getMaxX() || warp.getY() < level.getMinBuildHeight() ||
                warp.getY() > level.getMaxBuildHeight() || warp.getZ() >= level.getWorldBorder().getMaxZ();
    }
}