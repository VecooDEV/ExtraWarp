package com.vecoo.extrawarp.command;

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
import com.vecoo.extrawarp.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.UsernameCache;

import java.util.*;
import java.util.stream.Collectors;

public class WarpCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("warp")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp"))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(e -> execute(StringArgumentType.getString(e, "name"), e.getSource().getPlayerOrException())))

                .then(Commands.literal("set")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.set"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executeSet(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("pset")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.pset"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executePrivateSet(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("delete")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.delete"))
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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.private"))
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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.invite"))
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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.uninvite"))
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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.blacklist"))
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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.public"))
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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.rename"))
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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.welcome"))
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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.reload"))
                        .executes(e -> executeReload(e.getSource())))

                .then(Commands.literal("assets")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.assets"))
                        .executes(e -> executeAssets(e.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.assets.player"))
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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.top"))
                        .executes(e -> executeTop(e.getSource())))

                .then(Commands.literal("info")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.info"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executeInfo(StringArgumentType.getString(e, "warp"), e.getSource()))))

                .then(Commands.literal("update")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.update"))
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

    private static final List<String> COMMAND_ARGUMENT = Arrays.asList("set", "pset", "delete", "private", "invite", "uninvite", "blacklist", "public", "rename", "welcome", "help", "reload", "assets", "info", "update");

    private static int execute(String name, ServerPlayerEntity player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID) && warp.isLocked() && !warp.getInvitePlayers().contains(playerUUID) && !player.hasPermissions(4)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPrivate()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (warp.getBlacklistPlayers().contains(playerUUID) && !player.hasPermissions(4)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getBlacklistWarp()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        ServerWorld world = UtilWorld.getWorldByName(warp.getDimensionName());

        if (world == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (warp.getX() >= world.getWorldBorder().getMaxX() || warp.getY() < 1 || warp.getY() > world.getMaxBuildHeight() || warp.getZ() >= world.getWorldBorder().getMaxZ()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpBorder()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!ExtraWarpFactory.teleportWarp(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!warp.getUniquePlayers().contains(playerUUID)) {
            warp.addUniquePlayer(playerUUID);
        }

        if (warp.getWelcomeText().isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getTeleportWarp()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
        } else {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getAddWelcome() + warp.getWelcomeText()), Util.NIL_UUID);
        }

        return 1;
    }

    private static int executeSet(String name, ServerPlayerEntity player) {
        int maxWarpsPlayer = Utils.maxCountWarp(player);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (ExtraWarpFactory.WarpProvider.getWarpsByPlayer(player.getUUID()).size() >= maxWarpsPlayer && !player.hasPermissions(4)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarpsPlayer))), Util.NIL_UUID);
            return 0;
        }

        if (COMMAND_ARGUMENT.contains(name)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getInvalidWarpArgument()), Util.NIL_UUID);
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getConfig().getMaxMaxCharactersWarp()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpMaxCharacters()), Util.NIL_UUID);
            return 0;
        }

        if (ExtraWarpFactory.WarpProvider.hasWarpByName(name)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!ExtraWarpFactory.WarpProvider.addWarp(new Warp(name, player, false))) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getSetWarp()
                .replace("%warp%", name)).append(UtilChat.clickableMessageCommand(localeConfig.getSetWarpAdditional(),
                "/warp private " + name).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, UtilChat.formatMessage(localeConfig.getHoverSetToPrivateWarp() + name))))), Util.NIL_UUID);
        return 1;
    }

    private static int executePrivateSet(String name, ServerPlayerEntity player) {
        int maxWarpsPlayer = Utils.maxCountWarp(player);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (ExtraWarpFactory.WarpProvider.getWarpsByPlayer(player.getUUID()).size() >= maxWarpsPlayer && !player.hasPermissions(4)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarpsPlayer))), Util.NIL_UUID);
            return 0;
        }

        if (COMMAND_ARGUMENT.contains(name)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getInvalidWarpArgument()), Util.NIL_UUID);
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getConfig().getMaxMaxCharactersWarp()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpMaxCharacters()), Util.NIL_UUID);
            return 0;
        }

        if (ExtraWarpFactory.WarpProvider.hasWarpByName(name)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!ExtraWarpFactory.WarpProvider.addWarp(new Warp(name, player, true))) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getSetWarpPrivate()
                .replace("%warp%", name)), Util.NIL_UUID);
        return 1;
    }

    private static int executeDelete(String name, CommandSource source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID()) && !source.hasPermission(4) && source.getEntity() != null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        if (!ExtraWarpFactory.WarpProvider.removeWarp(warp)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)), false);
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpRemoved()
                .replace("%warp%", warp.getName())), false);
        return 1;
    }

    private static int executeInfo(String name, CommandSource source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        String textLocked = warp.isLocked() ? localeConfig.getLocked() : localeConfig.getUnlocked();

        List<String> playerInviteName = new ArrayList<>();
        List<String> playerBlacklistName = new ArrayList<>();

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

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getInfoWarp()
                .replace("%warp%", warp.getName())
                .replace("%owner%", UtilPlayer.getPlayerName(warp.getOwnerUUID()))
                .replace("%x%", String.valueOf(warp.getX()))
                .replace("%y%", String.valueOf(warp.getY()))
                .replace("%z%", String.valueOf(warp.getZ()))
                .replace("%dimension%", warp.getDimensionName())
                .replace("%invitePlayers%", playerInviteName.toString())
                .replace("%blacklistPlayers%", playerBlacklistName.toString())
                .replace("%count%", String.valueOf(warp.getUniquePlayers().size()))
                .replace("%locked%", textLocked)), false);
        return 1;
    }

    private static int executeUpdate(String name, ServerPlayerEntity player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID()) && !player.hasPermissions(4)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        warp.updatePosition(player);

        player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpUpdate()
                .replace("%warp%", warp.getName())), Util.NIL_UUID);
        return 1;
    }

    private static int executeRename(String name, String newName, CommandSource source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID()) && !source.hasPermission(4)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        if (warp.getName().equals(newName)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getEqualsRename()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        warp.setName(newName);

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpRename()
                .replace("%warp%", warp.getName())
                .replace("%name%", newName)), false);

        return 1;
    }

    private static int executePrivate(String name, ServerPlayerEntity player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (warp.isLocked()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPrivated()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID()) && !player.hasPermissions(4)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        warp.setLocked(true);

        player.sendMessage(UtilChat.formatMessage(localeConfig.getPrivateWarp()
                .replace("%warp%", warp.getName())), Util.NIL_UUID);
        return 1;
    }

    private static int executePublic(String name, ServerPlayerEntity player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!warp.isLocked()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPubliced()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID()) && !player.hasPermissions(4)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        warp.setLocked(false);

        player.sendMessage(UtilChat.formatMessage(localeConfig.getPublicWarp()
                .replace("%warp%", warp.getName())), Util.NIL_UUID);
        return 1;
    }

    private static int executeInvite(String name, String playerName, ServerPlayerEntity player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!UtilPlayer.hasUUID(playerName)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)), Util.NIL_UUID);
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (playerUUID.equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (warp.getInvitePlayers().contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)), Util.NIL_UUID);
            return 0;
        }

        warp.addInvitePlayer(targetUUID);

        player.sendMessage(UtilChat.formatMessage(localeConfig.getInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)), Util.NIL_UUID);

        UtilPlayer.sendMessageUuid(targetUUID, UtilChat.formatMessage(localeConfig.getInvitedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", player.getName().getString())));
        return 1;
    }

    private static int executeUnInvite(String name, String playerName, ServerPlayerEntity player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!UtilPlayer.hasUUID(playerName)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)), Util.NIL_UUID);
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (player.getUUID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!warp.getInvitePlayers().contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)), Util.NIL_UUID);
            return 0;
        }

        warp.removeInvitePlayer(targetUUID);

        player.sendMessage(UtilChat.formatMessage(localeConfig.getUnInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)), Util.NIL_UUID);
        return 1;
    }

    private static int executeAddBlacklist(String name, String playerName, ServerPlayerEntity player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!UtilPlayer.hasUUID(playerName)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)), Util.NIL_UUID);
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (playerUUID.equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (warp.getBlacklistPlayers().contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)), Util.NIL_UUID);
            return 0;
        }

        warp.addBlacklistPlayer(targetUUID);

        player.sendMessage(UtilChat.formatMessage(localeConfig.getBlacklistAddedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemoveBlacklist(String name, String playerName, ServerPlayerEntity player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!UtilPlayer.hasUUID(playerName)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)), Util.NIL_UUID);
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (playerUUID.equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!warp.getBlacklistPlayers().contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)), Util.NIL_UUID);
            return 0;
        }

        warp.removeBlacklistPlayer(targetUUID);

        player.sendMessage(UtilChat.formatMessage(localeConfig.getBlacklistRemovedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)), Util.NIL_UUID);
        return 1;
    }

    private static int executeAssets(ServerPlayerEntity player) {
        Set<Warp> warps = ExtraWarpFactory.WarpProvider.getWarpsByPlayer(player.getUUID());
        Set<Warp> publicWarps = new HashSet<>();
        Set<Warp> privateWarps = new HashSet<>();

        for (Warp warp : warps) {
            if (!warp.isLocked()) {
                publicWarps.add(warp);
            } else {
                privateWarps.add(warp);
            }
        }

        player.sendMessage(UtilChat.formatMessage(ExtraWarp.getInstance().getLocale().getWarpAssets()
                .replace("%count%", String.valueOf(warps.size()))
                .replace("%maxCount%", String.valueOf(Utils.maxCountWarp(player)))
                .replace("%publicWarps%", publicWarps.stream().map(Warp::getName).collect(Collectors.joining(", ")))
                .replace("%privateWarps%", privateWarps.stream().map(Warp::getName).collect(Collectors.joining(", ")))), Util.NIL_UUID);
        return 1;
    }

    private static int executeAssetsPlayer(CommandSource source, String playerName) {
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (!UtilPlayer.hasUUID(playerName)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)), false);
            return 0;
        }

        UUID playerUUID = UtilPlayer.getUUID(playerName);

        Set<Warp> warps = ExtraWarpFactory.WarpProvider.getWarpsByPlayer(playerUUID);
        Set<Warp> publicWarps = new HashSet<>();
        Set<Warp> privateWarps = new HashSet<>();

        for (Warp warp : warps) {
            if (!warp.isLocked()) {
                publicWarps.add(warp);
            } else {
                privateWarps.add(warp);
            }
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpAssetsPlayer()
                .replace("%player%", UtilPlayer.getPlayerName(playerUUID))
                .replace("%publicWarps%", publicWarps.stream().map(Warp::getName).collect(Collectors.joining(", ")))
                .replace("%privateWarps%", privateWarps.stream().map(Warp::getName).collect(Collectors.joining(", ")))), false);
        return 1;
    }

    private static int executeTop(CommandSource source) {
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();
        List<Warp> warps = new ArrayList<>(ExtraWarpFactory.WarpProvider.getWarps());
        warps.sort(Comparator.comparingInt(Warp::getUniquePlayersCount).reversed());

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getTopWarpTitle()), false);

        int count = 0;
        for (Warp warp : warps) {
            if (warp.isLocked()) {
                continue;
            }

            source.sendSuccess(UtilChat.clickableMessageCommand(localeConfig.getTopWarp()
                    .replace("%place%", localeConfig.getPlaces().get(count))
                    .replace("%warp%", warp.getName())
                    .replace("%player%", UtilPlayer.getPlayerName(warp.getOwnerUUID())), "/warp " + warp.getName()).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, UtilChat.formatMessage(localeConfig.getHoverTopWarp() + warp.getName())))), false);
            count++;
            if (count >= 10) {
                break;
            }
        }
        return 1;
    }

    private static int executeSetWelcome(String name, ITextComponent component, CommandSource source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID()) && !source.hasPermission(4)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        warp.setWelcomeText(component.getString());

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpSetWelcome()
                .replace("%warp%", warp.getName())), false);
        return 1;
    }

    private static int executeRemoveWelcome(String name, CommandSource source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID()) && !source.hasPermission(4)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        if (warp.getWelcomeText().isEmpty()) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpWelcomeEmpty()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        warp.setWelcomeText("");

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpRemoveWelcome()
                .replace("%warp%", warp.getName())), false);
        return 1;
    }

    private static int executeHelp(CommandSource source) {
        source.sendSuccess(UtilChat.formatMessage(ExtraWarp.getInstance().getLocale().getHelp()), false);
        return 1;
    }

    private static int executeReload(CommandSource source) {
        ExtraWarp.getInstance().loadConfig();

        source.sendSuccess(UtilChat.formatMessage(ExtraWarp.getInstance().getLocale().getReload()), false);
        return 1;
    }
}