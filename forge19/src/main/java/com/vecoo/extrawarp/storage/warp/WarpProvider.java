package com.vecoo.extrawarp.storage.warp;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.server.MinecraftServer;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class WarpProvider {
    private transient final String filePath;
    private final Set<Warp> warps;

    public WarpProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);

        this.warps = ConcurrentHashMap.newKeySet();
    }

    public Set<Warp> getWarps() {
        return this.warps;
    }

    public boolean addWarp(Warp warp) {
        if (!this.warps.add(warp)) {
            ExtraWarp.getLogger().error("[ExtraWarp] An error occurred while creating the warp: " + warp.getName());
            return false;
        }

        write();
        return true;
    }

    public boolean removeWarp(Warp warp) {
        if (!this.warps.remove(warp)) {
            ExtraWarp.getLogger().error("[ExtraWarp] An error occurred while remove the warp: " + warp.getName());
            return false;
        }

        write();
        return true;
    }

    public void write() {
        UtilGson.writeFileAsync(filePath, "WarpStorage.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        CompletableFuture<Boolean> future = UtilGson.readFileAsync(filePath, "WarpStorage.json", el -> this.warps.addAll(UtilGson.newGson().fromJson(el, WarpProvider.class).getWarps()));

        if (!future.join()) {
            write();
        }
    }
}