package de.aservo.confapi.confluence.service;


import com.atlassian.cache.CacheManager;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cache.CacheConfigManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.exception.NotFoundException;
import de.aservo.confapi.confluence.service.api.BackupService;
import de.aservo.confapi.confluence.service.api.CachesService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;

@Component
@ExportAsService(CachesService.class)
public class CachesServiceImpl implements CachesService {

    private final CacheConfigManager cacheConfigManager;
    private final CacheManager cacheManager;

    @Inject
    public CachesServiceImpl(
            @ComponentImport CacheConfigManager cacheSettingsManager,
            @ComponentImport CacheManager cacheManager) {

        this.cacheConfigManager = cacheSettingsManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean exists(String name) {
        return cacheManager.getManagedCache(name) != null;
    }

    @Override
    public Collection<ManagedCache> getAllManagedCaches() {
        Collection<ManagedCache> cacheList = cacheManager.getManagedCaches();
        //TODO: error handling
        return cacheList;
    }

    @Override
    public ManagedCache getCache(String name) {
        ManagedCache cache = cacheManager.getManagedCache(name);
        //TODO: error handling
        return cache;
    }


    @Override
    public void setMaxCacheSize(String name, int newValue) {
        ManagedCache cache = cacheManager.getManagedCache(name);
        if (cache != null) {
            cacheConfigManager.changeMaxCacheSize(name, newValue);
        } else {
            throw new NotFoundException(String.format(
                    "Given cache with name '%s' not found", name));
        }
        //TODO: error handling
    }
}
