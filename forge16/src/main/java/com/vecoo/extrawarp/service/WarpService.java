package com.vecoo.extrawarp.service;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

;

@Getter
public class WarpService {
    @Nonnull
    private transient final String filePath;
    @Nonnull
    private final List<Warp> warps;

    private transient volatile boolean dirty = false;

    public WarpService(@Nonnull String filePath, @Nonnull MinecraftServer server) {
        this.filePath = UtilWorld.resolveWorldDirectory(filePath, server);

        this.warps = new ArrayList<>();
    }

    public void addWarp(@Nonnull Warp warp) {
        this.warps.add(warp);
        this.dirty = true;
    }

    public boolean removeWarp(@Nonnull Warp warp) {
        if (!this.warps.remove(warp)) {
            ExtraWarp.getLogger().error("An error occurred while remove the warp {}.", warp.getName());
            return false;
        }

        this.dirty = true;
        return true;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void save() {
        UtilGson.writeFileAsync(this.filePath, "warps.json", UtilGson.getGson().toJson(this)).join();
    }

    private void saveInterval() {
        TaskTimer.builder()
                .withoutDelay()
                .interval(120 * 20L)
                .infinite()
                .consume(task -> {
                    if (ExtraWarp.getInstance().getServer().isRunning() && this.dirty) {
                        UtilGson.writeFileAsync(this.filePath, "warps.json",
                                UtilGson.getGson().toJson(this)).thenRun(() -> this.dirty = false);
                    }
                })
                .build();
    }

    public void init() {
        this.warps.clear();

        UtilGson.readFileAsync(this.filePath, "warps.json",
                el -> this.warps.addAll(UtilGson.getGson().fromJson(el, WarpService.class).getWarps())).join();

        saveInterval();
    }
}