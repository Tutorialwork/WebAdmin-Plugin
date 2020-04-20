package de.tutorialwork.webadmin.utils;

import java.util.HashMap;

public class Cache {

    private HashMap<String, String> nameCache;

    public Cache() {
        this.nameCache = new HashMap<>();
    }

    public HashMap<String, String> getNameCache() {
        return nameCache;
    }

    public void setNameCache(HashMap<String, String> nameCache) {
        this.nameCache = nameCache;
    }

    public void deleteNameCache(String UUID){
        if(this.nameCache.get(UUID) != null){
            this.nameCache.remove(UUID);
        }
    }

    public void cacheName(String Name, String UUID){
        this.deleteNameCache(UUID);
        this.nameCache.put(UUID, Name);
    }

    public boolean isCached(String UUID){
        if(this.nameCache != null){
            if(this.nameCache.get(UUID) == null){
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
