package com.vecoo.extrawarp.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@ConfigSerializable
public class LocaleConfig {
    private String reload = "&e(!) Configs have been reloaded.";
    private String setWarp = "&e(!) You have successfully set warp %warp%.";
    private String setWarpAdditional = " &eClick me to private warp.";
    private String setWarpPrivate = "&e(!) You have successfully set private warp %warp%.";
    private String teleportWarp = "&e(!) You have successfully teleported to warp %warp%.";
    private String privateWarp = "&e(!) You have successfully made the warp %warp% private.";
    private String publicWarp = "&e(!) You have successfully made the warp %warp% public.";
    private String inviteWarp = "&e(!) Player %player% has been successfully invited to your warp %warp%.";
    private String invitedWarp = "&e(!) Player %player% has invited you to warp %warp%.";
    private String unInviteWarp = "&e(!) Player %player% has been successfully uninvited to your warp %warp%.";
    private String blacklistAddedWarp = "&e(!) Player %player% has been successfully added blacklist to your warp %warp%.";
    private String blacklistRemovedWarp = "&e(!) Player %player% has been successfully removed blacklist to your warp %warp%.";
    private String warpRemoved = "&e(!) Warp %warp% successfully removed.";
    private String warpUpdate = "&e(!) You have successfully updated the warp %warp% position.";
    private String warpSetWelcome = "&e(!) You have successfully installed a welcome message on warp %warp%.";
    private String warpRemoveWelcome = "&e(!) You have successfully removed the welcome message from the warp %warp%.";
    private String warpRename = "&e(!) You have successfully changed the warp name from %warp% to %name%.";
    private String help = """
            &eInformation the ExtraWarp:
            /warp help - help with the ExtraWarp mod.
            /warp <name> - teleport to warp.
            /warp set <name> - set public warp at your position.
            /warp pset <name> - set private warp at your position.
            /warp delete <name> - delete your warp.
            /warp private <name> - make your warp private.
            /warp public <name> - make your warp public.
            /warp invite <name> <player> - invite a player to your warp.
            /warp uninvite <name> <player> - revoke an invitation to your warp.
            /warp blacklist add <name> <player> - add a player to your warp's blacklist.
            /warp blacklist remove <name> <player> - remove a player from your warp's blacklist.
            /warp rename <name> <newName> - rename your warp.
            /warp welcome - remove the welcome message from your warp.
            /warp welcome <message> - add a welcome message to your warp.
            /warp info <name> - view information about the warp.
            /warp update <name> - update the position of your warp.
            /warp assets - list of your warps.
            /warp top - list of popular warp""";
    private String topWarpTitle = "&e&lTop warps:";
    private String warpAssets = """
            &e&lYour warps (%count%/%maxCount%):
            &r&e- Public: %publicWarps%
            &e- Private: %privateWarps%""";
    private String warpAssetsPlayer = """
            &e&lWarps player %player%:
            &r&e- Public: %publicWarps%
            &e- Private: %privateWarps%""";
    private String infoWarp = """
            &e&lInfo warp %warp% (%locked%&e&l):
            &r&e- Owner: %owner%
            - X: %x%, Y: %y%, Z: %z% (%dimension%&e)
            - Invite players: %invitePlayers%
            - Blacklist players: %blacklistPlayers%
            - Unique players: %count%""";

    private String errorReload = "&c(!) Reload error, checking console and fixes config.";
    private String warpNotOwner = "&c(!) You are not the owner of the warp %warp%.";
    private String warpNotFound = "&c(!) Warp %warp% not found.";
    private String warpExist = "&c(!) Warp %warp% already exists.";
    private String warpWelcomeEmpty = "&c(!) You cannot delete the warp %warp% greeting message because it does not exist.";
    private String warpNotYourself = "&c(!) You cannot add/remove yourself to warp %warp% lists.";
    private String warpPrivated = "&c(!) Warp %warp% already private.";
    private String warpPubliced = "&c(!) Warp %warp% already public.";
    private String warpPrivate = "&c(!) Warp %warp% is private.";
    private String warpBorder = "&c(!) Warp %warp% is located beyond the world";
    private String warpNotDimension = "&c(!) Warp %warp% is not a dimension.";
    private String blacklistWarp = "&c(!) You are on warp %warp% blacklist.";
    private String warpPlayerAlready = "&c(!) Player %player% is already on one of the warp %warp% lists.";
    private String warpError = "&c(!) Error teleporting to warp %warp%. Try again or ask the warp owner to change the respawn point.";
    private String playerNotFound = "&c(!) Player %player% not found.";
    private String equalsRename = "&c(!) You cannot change the name of the warp %warp% because it is the same as what you plan to rename it to.";
    private String invalidWarpArgument = "&c(!) Your warp cannot contain arguments to the command itself in its name.";
    private String warpMaxCharacters = "&c(!) Your warp cannot contain more than 10 characters.";
    private String maxWarp = "&c(!) You cannot create a warp because your limit of all warps is - %count%.";

    private String topWarp = "&e%place%. %warp% by %player%";
    private String hoverSetToPrivateWarp = "/warp private ";
    private String hoverTopWarp = "/warp ";
    private List<String> places = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    private String addWelcome = "&e(!) ";
    private String locked = "&4Private&r&l";
    private String unlocked = "&2Public&r&l";
}