package com.vecoo.extrawarp.storage;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class WarpProvider {
    private transient final String filePath;
    private final Set<Warp> warps;

    private transient boolean intervalStarted = false;
    private transient volatile boolean dirty = false;

    public WarpProvider(@NotNull String filePath, @NotNull MinecraftServer server) {
        this.filePath = UtilWorld.resolveWorldDirectory(filePath, server);

        this.warps = new HashSet<>();
    }

    @NotNull
    public Set<Warp> getWarps() {
        return this.warps;
    }

    public void update() {
        this.dirty = true;
    }

    public boolean addWarp(@NotNull Warp warp) {
        if (!this.warps.add(warp)) {
            ExtraWarp.getLogger().error("An error occurred while creating the warp {}.", warp.getName());
            return false;
        }

        this.dirty = true;
        return true;
    }

    public boolean removeWarp(@NotNull Warp warp) {
        if (!this.warps.remove(warp)) {
            ExtraWarp.getLogger().error("An error occurred while remove the warp {}.", warp.getName());
            return false;
        }

        this.dirty = true;
        return true;
    }

    public void save() {
        UtilGson.writeFileAsync(this.filePath, "warps.json", UtilGson.getGson().toJson(this)).join();
    }

    private void saveInterval() {
        if (!this.intervalStarted) {
            TaskTimer.builder()
                    .withoutDelay()
                    .interval(150 * 20L)
                    .infinite()
                    .consume(task -> {
                        if (ExtraWarp.getInstance().getServer().isRunning() && this.dirty) {
                            UtilGson.writeFileAsync(this.filePath, "warps.json",
                                    UtilGson.getGson().toJson(this)).thenRun(() -> this.dirty = false);
                        }
                    })
                    .build();


            this.intervalStarted = true;
        }
    }

    public void init() {
        this.warps.clear();

        UtilGson.readFileAsync(this.filePath, "warps.json",
                el -> this.warps.addAll(UtilGson.getGson().fromJson(el, WarpProvider.class).getWarps())).join();

        saveInterval();
    }
}