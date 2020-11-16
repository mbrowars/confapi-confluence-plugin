package de.aservo.confapi.confluence.service.api;

import com.atlassian.cache.ManagedCache;

import java.util.Collection;

public interface CachesService {

    boolean exists(String name);

    Collection<ManagedCache> getAllManagedCaches();

    ManagedCache getCache(String name);

    void setMaxCacheSize(String name, int newValue);

}
