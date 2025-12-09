package com.vecoo.extrawarp.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extrawarp.ExtraWarp;

import java.util.Set;

public class ServerConfig {
    private int baseCountWarp = 2;
    private int maxCharactersWarp = 10;
    private Set<String> blockedNamesWarp = Sets.newHashSet("Name");
    private Set<String> permissionList = Sets.newHashSet("extrawarp.count.3", "extrawarp.count.5", "extrawarp.count.7");

    public int getBaseCountWarp() {
        return this.baseCountWarp;
    }

    public int getMaxCharactersWarp() {
        return this.maxCharactersWarp;
    }

    public Set<String> getBlockedNamesWarp() {
        return this.blockedNamesWarp;
    }

    public Set<String> getPermissionList() {
        return this.permissionList;
    }

    private void save() {
        UtilGson.writeFileAsync("/config/ExtraWarp/", "config.json", UtilGson.getGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/ExtraWarp/", "config.json", el -> {
            ServerConfig config = UtilGson.getGson().fromJson(el, ServerConfig.class);

            this.baseCountWarp = config.getBaseCountWarp();
            this.maxCharactersWarp = config.getMaxCharactersWarp();
            this.blockedNamesWarp = config.getBlockedNamesWarp();
            this.permissionList = config.getPermissionList();
        }).join();

        if (!completed) {
            ExtraWarp.getLogger().error("Error init config, generating new config.");
            save();
        }
    }
}
