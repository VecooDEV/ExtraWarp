package com.vecoo.extrawarp.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.api.factory.ExtraWarpFactory;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.storage.warp.Warp;
import com.vecoo.extrawarp.util.PermissionNodes;
import com.vecoo.extrawarp.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.UsernameCache;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class WarpCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("warp")
                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_COMMAND))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(e -> execute(StringArgumentType.getString(e, "name"), e.getSource().getPlayerOrException())))

                .then(Commands.literal("set")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_SET_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executeSet(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("pset")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.PRIVATE_WARP_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executePrivateSet(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("delete")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_DELETE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeDelete(StringArgumentType.getString(e, "warp"), e.getSource()))))

                .then(Commands.literal("private")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_PRIVATE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (!warp.isLocked()) {
                                            if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                builder.suggest(warp.getName());
                                            }
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executePrivate(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("invite")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_INVITE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("player", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (String nick : s.getSource().getOnlinePlayerNames()) {
                                                if (nick.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(nick);
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(e -> executeInvite(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "player"), e.getSource().getPlayerOrException())))))

                .then(Commands.literal("uninvite")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_UNINVITE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("player", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                for (UUID uuid : warp.getInvitePlayers()) {
                                                    String name = UtilPlayer.getPlayerName(uuid);
                                                    if (name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                        builder.suggest(name);
                                                    }
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(e -> executeUnInvite(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "player"), e.getSource().getPlayerOrException())))))

                .then(Commands.literal("blacklist")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_BLACKLIST_COMMAND))
                        .then(Commands.literal("add")
                                .then(Commands.argument("warp", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(warp.getName());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("player", StringArgumentType.string())
                                                .suggests((s, builder) -> {
                                                    for (String nick : s.getSource().getOnlinePlayerNames()) {
                                                        if (nick.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                            builder.suggest(nick);
                                                        }
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(e -> executeAddBlacklist(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "player"), e.getSource().getPlayerOrException())))))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("warp", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(warp.getName());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("player", StringArgumentType.string())
                                                .suggests((s, builder) -> {
                                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                        for (UUID uuid : warp.getBlacklistPlayers()) {
                                                            String name = UtilPlayer.getPlayerName(uuid);
                                                            if (name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                                builder.suggest(name);
                                                            }
                                                        }
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(e -> executeRemoveBlacklist(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "player"), e.getSource().getPlayerOrException()))))))

                .then(Commands.literal("public")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_PUBLIC_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.isLocked()) {
                                            if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                builder.suggest(warp.getName());
                                            }
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executePublic(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("rename")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_RENAME_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(e -> executeRename(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "name"), e.getSource())))))

                .then(Commands.literal("welcome")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_WELCOME_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeRemoveWelcome(StringArgumentType.getString(e, "warp"), e.getSource()))
                                .then(Commands.argument("message", MessageArgument.message())
                                        .executes(e -> executeSetWelcome(StringArgumentType.getString(e, "warp"), MessageArgument.getMessage(e, "message"), e.getSource())))))

                .then(Commands.literal("help")
                        .executes(e -> executeHelp(e.getSource())))

                .then(Commands.literal("reload")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_RELOAD_COMMAND))
                        .executes(e -> executeReload(e.getSource())))

                .then(Commands.literal("assets")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_ASSETS_COMMAND))
                        .executes(e -> executeAssets(e.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_ASSETS_PLAYER_COMMAND))
                                .suggests((s, builder) -> {
                                    for (String nick : s.getSource().getOnlinePlayerNames()) {
                                        if (nick.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(nick);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeAssetsPlayer(e.getSource(), StringArgumentType.getString(e, "player")))))

                .then(Commands.literal("top")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_TOP_COMMAND))
                        .executes(e -> executeTop(e.getSource())))

                .then(Commands.literal("info")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_INFO_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executeInfo(StringArgumentType.getString(e, "warp"), e.getSource()))))

                .then(Commands.literal("update")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_UPDATE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeUpdate(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException())))));
    }

    private static final Set<String> COMMAND_ARGUMENT = Sets.newHashSet("set", "pset", "delete", "private", "invite", "uninvite", "blacklist", "public", "rename", "welcome", "help", "reload", "assets", "info", "update");

    private static int execute(@NotNull String name, @NotNull ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID) && warp.isLocked() && !warp.getInvitePlayers().contains(playerUUID)
                && !UtilPermission.hasPermission(player, PermissionNodes.WARP_BYPASS)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPrivate()
                    .replace("%warp%", name)));
            return 0;
        }

        if (warp.getBlacklistPlayers().contains(playerUUID)
                && !UtilPermission.hasPermission(player, PermissionNodes.WARP_BYPASS)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getBlacklistWarp()
                    .replace("%warp%", name)));
            return 0;
        }

        ServerLevel level = UtilWorld.getLevelByName(warp.getDimensionName());

        if (level == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getX() >= level.getWorldBorder().getMaxX() || warp.getY() < level.getMinBuildHeight() ||
                warp.getY() > level.getMaxBuildHeight() || warp.getZ() >= level.getWorldBorder().getMaxZ()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpBorder()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!ExtraWarpFactory.teleportWarp(player, warp)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.getUniquePlayers().contains(playerUUID)) {
            warp.addUniquePlayer(playerUUID);
        }

        if (warp.getWelcomeText().isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getTeleportWarp()
                    .replace("%warp%", warp.getName())));
        } else {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getAddWelcome() + warp.getWelcomeText()));
        }

        return 1;
    }

    private static int executeSet(@NotNull String name, @NotNull ServerPlayer player) {
        int maxWarpsPlayer = Utils.maxCountWarp(player);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (ExtraWarpFactory.WarpProvider.getWarpsByPlayer(player.getUUID()).size() >= maxWarpsPlayer
                && !UtilPermission.hasPermission(player, PermissionNodes.WARP_BYPASS)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarpsPlayer))));
            return 0;
        }

        if (COMMAND_ARGUMENT.contains(name)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getInvalidWarpArgument()));
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getConfig().getMaxMaxCharactersWarp()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpMaxCharacters()));
            return 0;
        }

        if (ExtraWarpFactory.WarpProvider.hasWarpByName(name)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!ExtraWarpFactory.WarpProvider.addWarp(new Warp(name, player, false))) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSetWarp()
                .replace("%warp%", name)).copy().append(UtilChat.clickableMessageCommand(localeConfig.getSetWarpAdditional(),
                "/warp private " + name).copy()
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                UtilChat.formatMessage(localeConfig.getHoverSetToPrivateWarp() + name))))));
        return 1;
    }

    private static int executePrivateSet(@NotNull String name, @NotNull ServerPlayer player) {
        int maxWarpsPlayer = Utils.maxCountWarp(player);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (ExtraWarpFactory.WarpProvider.getWarpsByPlayer(player.getUUID()).size() >= maxWarpsPlayer
                && !UtilPermission.hasPermission(player, PermissionNodes.WARP_BYPASS)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarpsPlayer))));
            return 0;
        }

        if (COMMAND_ARGUMENT.contains(name)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getInvalidWarpArgument()));
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getConfig().getMaxMaxCharactersWarp()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpMaxCharacters()));
            return 0;
        }

        if (ExtraWarpFactory.WarpProvider.hasWarpByName(name)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!ExtraWarpFactory.WarpProvider.addWarp(new Warp(name, player, true))) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSetWarpPrivate()
                .replace("%warp%", name)));
        return 1;
    }

    private static int executeDelete(@NotNull String name, @NotNull CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID())
                && !UtilPermission.hasPermission(source, PermissionNodes.WARP_BYPASS) && source.getEntity() != null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!ExtraWarpFactory.WarpProvider.removeWarp(warp)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)));
            return 0;
        }

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpRemoved()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeInfo(@NotNull String name, @NotNull CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        Set<String> playerInviteName = new HashSet<>();
        Set<String> playerBlacklistName = new HashSet<>();

        for (UUID playerUUID : warp.getInvitePlayers()) {
            if (UsernameCache.containsUUID(playerUUID)) {
                playerInviteName.add(UtilPlayer.getPlayerName(playerUUID));
            }
        }

        for (UUID playerUUID : warp.getBlacklistPlayers()) {
            if (UsernameCache.containsUUID(playerUUID)) {
                playerBlacklistName.add(UtilPlayer.getPlayerName(playerUUID));
            }
        }

        ServerPlayer player = source.getPlayer();
        boolean hideXYZ = player != null && warp.isLocked() && !UtilPermission.hasPermission(player, PermissionNodes.WARP_BYPASS)
                && !warp.getOwnerUUID().equals(player.getUUID()) && !warp.getInvitePlayers().contains(player.getUUID());

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getInfoWarp()
                .replace("%warp%", warp.getName())
                .replace("%owner%", UtilPlayer.getPlayerName(warp.getOwnerUUID()))
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

    private static int executeUpdate(@NotNull String name, @NotNull ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID())
                && !UtilPermission.hasPermission(player, PermissionNodes.WARP_BYPASS)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        ServerLevel level = UtilWorld.getLevelByName(warp.getDimensionName());

        if (level == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getX() >= level.getWorldBorder().getMaxX() || warp.getY() < level.getMinBuildHeight() ||
                warp.getY() > level.getMaxBuildHeight() || warp.getZ() >= level.getWorldBorder().getMaxZ()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpBorder()
                    .replace("%warp%", name)));
            return 0;
        }

        warp.updatePosition(player);
        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpUpdate()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeRename(@NotNull String name, @NotNull String newName, @NotNull CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID())
                && !UtilPermission.hasPermission(source, PermissionNodes.WARP_BYPASS)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getName().equals(newName)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getEqualsRename()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (ExtraWarpFactory.WarpProvider.hasWarpByName(newName)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", newName)));
            return 0;
        }

        warp.setName(newName);
        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpRename()
                .replace("%warp%", warp.getName())
                .replace("%name%", newName)));

        return 1;
    }

    private static int executePrivate(@NotNull String name, @NotNull ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (warp.isLocked()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPrivated()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID())
                && !UtilPermission.hasPermission(player, PermissionNodes.WARP_BYPASS)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.setLocked(true);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPrivateWarp()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executePublic(@NotNull String name, @NotNull ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!warp.isLocked()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPubliced()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID())
                && !UtilPermission.hasPermission(player, PermissionNodes.WARP_BYPASS)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.setLocked(false);
        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPublicWarp()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeInvite(@NotNull String name, @NotNull String playerName, @NotNull ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (targetUUID == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (playerUUID.equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.addInvitePlayer(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)));

        UtilPlayer.sendMessageUuid(targetUUID, UtilChat.formatMessage(localeConfig.getInvitedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", player.getName().getString())));
        return 1;
    }

    private static int executeUnInvite(@NotNull String name, @NotNull String playerName, @NotNull ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (targetUUID == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID())) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.removeInvitePlayer(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getUnInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)));
        return 1;
    }

    private static int executeAddBlacklist(@NotNull String name, @NotNull String playerName, @NotNull ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (targetUUID == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID())) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.addBlacklistPlayer(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getBlacklistAddedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)));
        return 1;
    }

    private static int executeRemoveBlacklist(@NotNull String name, @NotNull String playerName, @NotNull ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (targetUUID == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID())) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.removeBlacklistPlayer(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getBlacklistRemovedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)));
        return 1;
    }

    private static int executeAssets(@NotNull ServerPlayer player) {
        Set<Warp> warps = ExtraWarpFactory.WarpProvider.getWarpsByPlayer(player.getUUID());

        String publicWarps = warps.stream()
                .filter(warp -> !warp.isLocked())
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        String privateWarps = warps.stream()
                .filter(Warp::isLocked)
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        player.sendSystemMessage(UtilChat.formatMessage(ExtraWarp.getInstance().getLocale().getWarpAssets()
                .replace("%count%", String.valueOf(warps.size()))
                .replace("%maxCount%", String.valueOf(Utils.maxCountWarp(player)))
                .replace("%publicWarps%", publicWarps)
                .replace("%privateWarps%", privateWarps)));
        return 1;
    }

    private static int executeAssetsPlayer(@NotNull CommandSourceStack source, @NotNull String playerName) {
        UUID targetUUID = UtilPlayer.getUUID(playerName);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (targetUUID == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        Set<Warp> warps = ExtraWarpFactory.WarpProvider.getWarpsByPlayer(targetUUID);

        String publicWarps = warps.stream()
                .filter(warp -> !warp.isLocked())
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        String privateWarps = warps.stream()
                .filter(Warp::isLocked)
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpAssetsPlayer()
                .replace("%player%", playerName)
                .replace("%publicWarps%", publicWarps)
                .replace("%privateWarps%", privateWarps)));
        return 1;
    }

    private static int executeTop(@NotNull CommandSourceStack source) {
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();
        List<Warp> topWarps = ExtraWarpFactory.WarpProvider.getWarps().stream()
                .filter(warp -> !warp.isLocked())
                .sorted(Comparator.comparingInt((Warp warp) -> warp.getUniquePlayers().size()).reversed())
                .limit(10)
                .toList();

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getTopWarpTitle()));

        for (int i = 0; i < topWarps.size(); i++) {
            Warp warp = topWarps.get(i);

            source.sendSystemMessage(UtilChat.clickableMessageCommand(localeConfig.getTopWarp()
                    .replace("%place%", localeConfig.getPlaces().get(i))
                    .replace("%warp%", warp.getName())
                    .replace("%player%", UtilPlayer.getPlayerName(warp.getOwnerUUID())), "/warp " +
                    warp.getName()).copy().withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    UtilChat.formatMessage(localeConfig.getHoverTopWarp() + warp.getName())))));
        }

        return 1;
    }

    private static int executeSetWelcome(@NotNull String name, Component component, @NotNull CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID())
                && !UtilPermission.hasPermission(source, PermissionNodes.WARP_BYPASS)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.setWelcomeText(component.getString());
        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpSetWelcome()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeRemoveWelcome(@NotNull String name, @NotNull CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID())
                && !UtilPermission.hasPermission(source, PermissionNodes.WARP_BYPASS)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getWelcomeText().isEmpty()) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpWelcomeEmpty()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.setWelcomeText(null);
        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpRemoveWelcome()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeHelp(@NotNull CommandSourceStack source) {
        source.sendSystemMessage(UtilChat.formatMessage(ExtraWarp.getInstance().getLocale().getHelp()));
        return 1;
    }

    private static int executeReload(@NotNull CommandSourceStack source) {
        ExtraWarp.getInstance().loadConfig();
        ExtraWarp.getInstance().loadStorage();

        source.sendSystemMessage(UtilChat.formatMessage(ExtraWarp.getInstance().getLocale().getReload()));
        return 1;
    }
}