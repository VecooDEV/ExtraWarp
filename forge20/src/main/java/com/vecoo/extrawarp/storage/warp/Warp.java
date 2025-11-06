package com.vecoo.extrawarp.storage.warp;

import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Warp {
    private String name;
    private double x, y, z;
    private float xRot, yRot;
    private String dimensionName;
    private final UUID ownerUUID;
    private final Set<UUID> invitePlayers, blacklistPlayers, uniquePlayers;
    private String welcomeText;
    private boolean locked;

    public Warp(@NotNull String name, @NotNull ServerPlayer player, boolean isLocked) {
        this.name = name;
        this.x = getFormatted(player.getX());
        this.y = getFormatted(player.getY());
        this.z = getFormatted(player.getZ());
        this.xRot = getFormatted(player.getXRot());
        this.yRot = getFormatted(player.getYRot());
        this.dimensionName = player.level().dimension().location().getPath();
        this.ownerUUID = player.getUUID();
        this.invitePlayers = new HashSet<>();
        this.blacklistPlayers = new HashSet<>();
        this.uniquePlayers = new HashSet<>();
        this.welcomeText = "";
        this.locked = isLocked;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getXRot() {
        return this.xRot;
    }

    public float getYRot() {
        return this.yRot;
    }

    @NotNull
    public String getDimensionName() {
        return this.dimensionName;
    }

    @NotNull
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @NotNull
    public Set<UUID> getInvitePlayers() {
        return this.invitePlayers;
    }

    @NotNull
    public Set<UUID> getBlacklistPlayers() {
        return this.blacklistPlayers;
    }

    @NotNull
    public Set<UUID> getUniquePlayers() {
        return this.uniquePlayers;
    }

    @NotNull
    public String getWelcomeText() {
        return this.welcomeText;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setName(@NotNull String name) {
        this.name = name;
        ExtraWarp.getInstance().getWarpProvider().updateStorage();
    }

    public void setCoordinatePosition(double x, double y, double z, float xRot, float yRot) {
        this.x = getFormatted(x);
        this.y = getFormatted(y);
        this.z = getFormatted(z);
        this.xRot = getFormatted(xRot);
        this.yRot = getFormatted(yRot);
        ExtraWarp.getInstance().getWarpProvider().updateStorage();
    }

    public void updatePosition(@NotNull ServerPlayer player) {
        this.x = getFormatted(player.getX());
        this.y = getFormatted(player.getY());
        this.z = getFormatted(player.getZ());
        this.xRot = getFormatted(player.getXRot());
        this.yRot = getFormatted(player.getYRot());
        this.dimensionName = player.level().dimension().location().getPath();
        ExtraWarp.getInstance().getWarpProvider().updateStorage();
    }

    public void setDimensionName(@NotNull String dimensionName) {
        this.dimensionName = dimensionName;
        ExtraWarp.getInstance().getWarpProvider().updateStorage();
    }

    public boolean addInvitePlayer(@NotNull UUID playerUUID) {
        if (!this.invitePlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpProvider().updateStorage();
        return true;
    }

    public boolean removeInvitePlayer(@NotNull UUID playerUUID) {
        if (!this.invitePlayers.remove(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpProvider().updateStorage();
        return true;
    }

    public boolean addBlacklistPlayer(@NotNull UUID playerUUID) {
        if (!this.blacklistPlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpProvider().updateStorage();
        return true;
    }

    public boolean removeBlacklistPlayer(@NotNull UUID playerUUID) {
        if (!this.blacklistPlayers.remove(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpProvider().updateStorage();
        return true;
    }

    public boolean addUniquePlayer(@NotNull UUID playerUUID) {
        if (!this.uniquePlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpProvider().updateStorage();
        return true;
    }

    public void setWelcomeText(@Nullable String text) {
        this.welcomeText = text == null ? "" : text;
        ExtraWarp.getInstance().getWarpProvider().updateStorage();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        ExtraWarp.getInstance().getWarpProvider().updateStorage();
    }

    private double getFormatted(double value) {
        return Double.parseDouble(String.format("%.2f", value));
    }

    private float getFormatted(float value) {
        return Math.round(value * 100F) / 100F;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return this.name.equalsIgnoreCase(((Warp) object).name);
    }

    @Override
    public int hashCode() {
        return this.name.toLowerCase().hashCode();
    }
}