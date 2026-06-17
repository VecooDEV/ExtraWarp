package com.vecoo.extrawarp.service;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Setting;
import lombok.*;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ConfigSerializable
@EqualsAndHashCode(of = "name")
public class Warp {
    @NotNull
    @Setter
    private String name;
    @Setter
    private double x, y, z;
    @Setter
    @Setting("xRot")
    private float xRot;
    @Setter
    @Setting("yRot")
    private float yRot;
    @NotNull
    @Setter
    @Setting("dimensionName")
    private String dimensionName;
    @NotNull
    @Setter
    @Setting("ownerUUID")
    private UUID ownerUUID;
    @NotNull
    @Setting("invitePlayers")
    private Set<UUID> invitePlayers = new HashSet<>();
    @NotNull
    @Setting("blacklistPlayers")
    private Set<UUID> blacklistPlayers = new HashSet<>();
    @NotNull
    @Setting("uniquePlayers")
    private Set<UUID> uniquePlayers = new HashSet<>();
    @NotNull
    @Setting("welcomeText")
    private String welcomeText;
    @Setter
    private boolean locked;

    @NotNull
    private transient final AtomicBoolean dirty = new AtomicBoolean(true);

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

    public void setCoordinatePosition(double x, double y, double z, float xRot, float yRot) {
        this.x = getFormatted(x);
        this.y = getFormatted(y);
        this.z = getFormatted(z);
        this.xRot = getFormatted(xRot);
        this.yRot = getFormatted(yRot);
    }

    public void updatePosition(@NotNull ServerPlayer player) {
        this.x = getFormatted(player.getX());
        this.y = getFormatted(player.getY());
        this.z = getFormatted(player.getZ());
        this.xRot = getFormatted(player.getXRot());
        this.yRot = getFormatted(player.getYRot());
        this.dimensionName = player.level().dimension().location().getPath();
    }

    public boolean addInvitePlayer(@NotNull UUID playerUUID) {
        return this.invitePlayers.add(playerUUID);
    }

    public boolean removeInvitePlayer(@NotNull UUID playerUUID) {
        return this.invitePlayers.remove(playerUUID);
    }

    public boolean addBlacklistPlayer(@NotNull UUID playerUUID) {
        return this.blacklistPlayers.add(playerUUID);
    }

    public boolean removeBlacklistPlayer(@NotNull UUID playerUUID) {
        return this.blacklistPlayers.remove(playerUUID);
    }

    public void addUniquePlayer(@NotNull UUID playerUUID) {
        this.uniquePlayers.add(playerUUID);
    }

    public void setWelcomeText(@Nullable String text) {
        this.welcomeText = text == null ? "" : text;
    }

    private double getFormatted(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private float getFormatted(float value) {
        return Math.round(value * 100.0F) / 100.0F;
    }

    @NotNull
    public Warp copy() {
        val storage = new Warp();

        storage.name = this.name;
        storage.x = this.x;
        storage.y = this.y;
        storage.z = this.z;
        storage.xRot = this.xRot;
        storage.yRot = this.yRot;
        storage.dimensionName = this.dimensionName;
        storage.ownerUUID = this.ownerUUID;
        storage.invitePlayers = new HashSet<>(this.invitePlayers);
        storage.blacklistPlayers = new HashSet<>(this.blacklistPlayers);
        storage.uniquePlayers = new HashSet<>(this.uniquePlayers);
        storage.welcomeText = this.welcomeText;
        storage.locked = this.locked;

        return storage;
    }
}