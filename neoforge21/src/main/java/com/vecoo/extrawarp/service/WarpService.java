package com.vecoo.extrawarp.service;

import com.vecoo.extralib.loader.GsonLoader;
import com.vecoo.extralib.scheduler.TaskTimer;
import com.vecoo.extralib.util.WorldUtil;
import com.vecoo.extrawarp.ExtraWarp;
import lombok.Getter;
import lombok.val;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Getter
public class WarpService {
    @NotNull
    private final Path filePath;
    @NotNull
    private final Map<String, Warp> storage = new ConcurrentHashMap<>();
    @NotNull
    private final Map<String, ReentrantLock> warpsLocks = new ConcurrentHashMap<>();

    @NotNull
    private final Map<UUID, Set<Warp>> playerWarpsCache = new ConcurrentHashMap<>();

    public WarpService(@NotNull String directory, @NotNull MinecraftServer server) {
        this.filePath = Path.of(WorldUtil.resolveWorldDirectory(directory, server));
    }

    @Nullable
    public Warp getStorage(@NotNull String warpName) {
        return this.storage.get(warpName.toLowerCase(Locale.ROOT));
    }

    public void addWarp(@NotNull Warp warp) {
        val lock = getLock(warp.getName());

        lock.lock();

        try {
            this.storage.put(warp.getName().toLowerCase(Locale.ROOT), warp);
            this.playerWarpsCache.computeIfAbsent(warp.getOwnerUUID(), uuid -> ConcurrentHashMap.newKeySet()).add(warp);
            warp.getDirty().set(true);
        } finally {
            lock.unlock();
        }
    }

    public boolean removeWarp(@NotNull String warpName) {
        val lock = getLock(warpName);

        lock.lock();

        try {
            val removedWarp = this.storage.remove(warpName.toLowerCase(Locale.ROOT));

            if (removedWarp != null) {
                val playerWarps = this.playerWarpsCache.get(removedWarp.getOwnerUUID());

                if (playerWarps != null) {
                    playerWarps.remove(removedWarp);

                    if (playerWarps.isEmpty()) {
                        this.playerWarpsCache.remove(removedWarp.getOwnerUUID());
                    }
                }

                File file = this.filePath.resolve(removedWarp.getName() + ".json").toFile();

                if (file.exists() && !file.delete()) {
                    ExtraWarp.getLogger().error("Failed to delete warp file: {}.json", warpName);
                }

                this.warpsLocks.remove(warpName.toLowerCase(Locale.ROOT));
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void modifyStorage(@NotNull String warpName, Consumer<Warp> consumer) {
        val storage = getStorage(warpName);

        if (storage == null) {
            return;
        }

        val lock = getLock(warpName);

        lock.lock();

        try {
            consumer.accept(storage);
            storage.getDirty().set(true);
        } finally {
            lock.unlock();
        }
    }

    public void save(boolean force) {
        for (Warp storage : this.storage.values()) {
            val lock = getLock(storage.getName());

            lock.lock();

            try {
                if (storage.getDirty().compareAndSet(true, false) || force) {
                    GsonLoader.save(storage.copy(), this.filePath.resolve(storage.getName() + ".json"));
                }
            } catch (IOException e) {
                storage.getDirty().set(true);
                ExtraWarp.getLogger().error(e.getMessage());
            } finally {
                lock.unlock();
            }
        }
    }

    private void saveInterval() {
        TaskTimer.builder()
                .delay(120 * 20L)
                .interval(120 * 20L)
                .infinite()
                .execute(() -> {
                    if (ExtraWarp.getInstance().getServer().isRunning()) {
                        for (Warp storage : this.storage.values()) {
                            Warp snapshot;
                            val lock = getLock(storage.getName());

                            lock.lock();

                            try {
                                if (!storage.getDirty().compareAndSet(true, false)) {
                                    continue;
                                }

                                snapshot = storage.copy();
                            } finally {
                                lock.unlock();
                            }

                            CompletableFuture.runAsync(() -> {
                                try {
                                    GsonLoader.save(snapshot, this.filePath.resolve(snapshot.getName() + ".json"));
                                } catch (IOException e) {
                                    storage.getDirty().set(true);
                                    ExtraWarp.getLogger().error("Async save error: ", e);
                                }
                            }, GsonLoader.WRITER_EXECUTOR);
                        }
                    }
                }).build();
    }

    public void init() throws IOException {
        if (!this.storage.isEmpty()) {
            return;
        }

        val list = this.filePath.toFile().listFiles((dir, name) -> name.endsWith(".json"));

        if (list == null) {
            return;
        }

        for (File file : list) {
            val storage = GsonLoader.load(Warp.class, file.toPath(), true);

            if (storage == null) {
                throw new IOException(String.format("Failed to load file: %s. Data reset, create backup.", file.toPath()));
            } else {
                this.storage.put(storage.getName().toLowerCase(Locale.ROOT), storage);
                this.playerWarpsCache.computeIfAbsent(storage.getOwnerUUID(), uuid -> ConcurrentHashMap.newKeySet()).add(storage);
            }
        }

        saveInterval();
    }

    @NotNull
    private ReentrantLock getLock(@NotNull String warpName) {
        return this.warpsLocks.computeIfAbsent(warpName.toLowerCase(Locale.ROOT), uuid -> new ReentrantLock());
    }
}