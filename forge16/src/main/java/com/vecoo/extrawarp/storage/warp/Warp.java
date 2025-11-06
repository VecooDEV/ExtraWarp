package com.vecoo.extrawarp.storage.warp;

import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public Warp(@Nonnull String name, @Nonnull ServerPlayerEntity player, boolean isLocked) {
        this.name = name;
        this.x = getFormatted(player.getX());
        this.y = getFormatted(player.getY());
        this.z = getFormatted(player.getZ());
        this.xRot = getFormatted(player.xRot);
        this.yRot = getFormatted(player.yRot);
        this.dimensionName = player.level.dimension().location().getPath();
        this.ownerUUID = player.getUUID();
        this.invitePlayers = new HashSet<>();
        this.blacklistPlayers = new HashSet<>();
        this.uniquePlayers = new HashSet<>();
        this.welcomeText = "";
        this.locked = isLocked;
    }

    @Nonnull
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

    @Nonnull
    public String getDimensionName() {
        return this.dimensionName;
    }

    @Nonnull
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Nonnull
    public Set<UUID> getInvitePlayers() {
        return this.invitePlayers;
    }

    @Nonnull
    public Set<UUID> getBlacklistPlayers() {
        return this.blacklistPlayers;
    }

    @Nonnull
    public Set<UUID> getUniquePlayers() {
        return this.uniquePlayers;
    }

    @Nonnull
    public String getWelcomeText() {
        return this.welcomeText;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setName(@Nonnull String name) {
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

    public void updatePosition(@Nonnull ServerPlayerEntity player) {
        this.x = getFormatted(player.getX());
        this.y = getFormatted(player.getY());
        this.z = getFormatted(player.getZ());
        this.xRot = getFormatted(player.xRot);
        this.yRot = getFormatted(player.yRot);
        this.dimensionName = player.level.dimension().location().getPath();
        ExtraWarp.getInstance().getWarpProvider().updateStorage();
    }

    public void setDimensionName(@Nonnull String dimensionName) {
        this.dimensionName = dimensionName;
        ExtraWarp.getInstance().getWarpProvider().updateStorage();
    }

    public boolean addInvitePlayer(@Nonnull UUID playerUUID) {
        if (!this.invitePlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpProvider().updateStorage();
        return true;
    }

    public boolean removeInvitePlayer(@Nonnull UUID playerUUID) {
        if (!this.invitePlayers.remove(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpProvider().updateStorage();
        return true;
    }

    public boolean addBlacklistPlayer(@Nonnull UUID playerUUID) {
        if (!this.blacklistPlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpProvider().updateStorage();
        return true;
    }

    public boolean removeBlacklistPlayer(@Nonnull UUID playerUUID) {
        if (!this.blacklistPlayers.remove(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpProvider().updateStorage();
        return true;
    }

    public boolean addUniquePlayer(@Nonnull UUID playerUUID) {
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