package de.aservo.confapi.confluence.service;


import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cache.CacheConfigManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import de.aservo.confapi.commons.exception.NotFoundException;
import de.aservo.confapi.confluence.model.CacheBean;
import de.aservo.confapi.confluence.service.api.CachesService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

@Component
@ExportAsService(CachesService.class)
public class CachesServiceImpl implements CachesService {

    private final CacheManager cacheManager;

    @Inject
    public CachesServiceImpl(
            @ComponentImport CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean exists(String name) {
        return cacheManager.getManagedCache(name) != null;
    }

    @Override
    public Collection<CacheBean> getAllCaches() {
        // TODO add names - not present jet
        Collection<ManagedCache> managedCacheList = cacheManager.getManagedCaches();
        Collection<CacheBean> cacheList = new ArrayList<>();

        managedCacheList.forEach(cache -> {

            // create new CacheBean
            CacheBean cacheBean = new CacheBean();
            cacheBean.setName(cache.getName());
            cacheBean.setCurrentHeapSize(cache.getStatistics().get(CacheStatisticsKey.HEAP_SIZE).get());

            cacheBean.setEffectivenessInPercent(getEffectiveness(cache));
            cacheBean.setSize(cache.currentMaxEntries());

            cacheBean.setUtilisationInPercent(getUtilization(cache));

            cacheList.add(cacheBean);
        });
        //TODO: error handling
        return cacheList;
    }

    @Override
    public CacheBean getCache(String name) {
        ManagedCache cache = cacheManager.getManagedCache(name);
        CacheBean cacheBean = new CacheBean();

        if (cache != null) {
            cacheBean.setName(cache.getName());
            cacheBean.setSize(cache.currentMaxEntries());
            cacheBean.setCurrentHeapSize(cache.getStatistics().get(CacheStatisticsKey.HEAP_SIZE).get());
            cacheBean.setEffectivenessInPercent(getEffectiveness(cache));
            cacheBean.setUtilisationInPercent(getUtilization(cache));
        } else {
            throw new NotFoundException(String.format(
                    "Given cache with name '%s' not found", name));
        }

        return cacheBean;
    }


    @Override
    public void setMaxCacheSize(String name, int newValue) {
        ManagedCache cache = cacheManager.getManagedCache(name);
        if (cache != null) {
            if (!cache.updateMaxEntries(newValue)) {
                throw new InternalServerErrorException(String.format(
                        "Given cache with name '%s' does not support cache resizing", name));
            }
        } else {
            throw new NotFoundException(String.format(
                    "Given cache with name '%s' not found", name));
        }
        //TODO: error handling
    }

    private double getEffectiveness(long hit, long miss) {
        return (double) hit / (hit + miss);
    }

    private double getEffectiveness(ManagedCache cache) {
        long hit = cache.getStatistics().get(CacheStatisticsKey.HIT_COUNT).get();
        long miss = cache.getStatistics().get(CacheStatisticsKey.MISS_COUNT).get();
        return (double) hit * 100 / (hit + miss);
    }

    private Double getUtilization(long objects, Integer size) {
        // currentMaxEntries can be null so check this first
        if (size != null) {
            return (double) objects * 100 / size;
        }
        return null;
    }

    private Double getUtilization(ManagedCache cache) {
        // currentMaxEntries can be null so check this first

        long objects = cache.getStatistics().get(CacheStatisticsKey.SIZE).get();
        Integer size = cache.currentMaxEntries();

        if (size != null) {
            return (double) objects * 100 / size;
        }
        return null;
    }
}
