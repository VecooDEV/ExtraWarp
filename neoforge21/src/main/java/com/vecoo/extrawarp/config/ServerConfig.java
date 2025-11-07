package com.vecoo.extrawarp.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extrawarp.ExtraWarp;

import java.util.Set;

public class ServerConfig {
    private static final int CURRENT_CONFIG_VERSION = 1;

    private int configVersion = 1;
    private int baseCountWarp = 2;
    private int maxMaxCharactersWarp = 10;
    private Set<String> blockedNamesWarp = Sets.newHashSet("Name");
    private Set<String> permissionList = Sets.newHashSet("extrawarp.count.3", "extrawarp.count.5", "extrawarp.count.7");

    public int getConfigVersion() {
        return this.configVersion;
    }

    public int getBaseCountWarp() {
        return this.baseCountWarp;
    }

    public int getMaxMaxCharactersWarp() {
        return this.maxMaxCharactersWarp;
    }

    public Set<String> getBlockedNamesWarp() {
        return this.blockedNamesWarp;
    }

    public Set<String> getPermissionList() {
        return this.permissionList;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ExtraWarp/", "config.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/ExtraWarp/", "config.json", el -> {
            ServerConfig config = UtilGson.newGson().fromJson(el, ServerConfig.class);

            this.configVersion = config.getConfigVersion();
            this.baseCountWarp = config.getBaseCountWarp();
            this.maxMaxCharactersWarp = config.getMaxMaxCharactersWarp();
            this.blockedNamesWarp = config.getBlockedNamesWarp();
            this.permissionList = config.getPermissionList();
        }).join();

        if (!completed) {
            ExtraWarp.getLogger().error("Error init config, generating new config.");
            write();
        }
    }
}
