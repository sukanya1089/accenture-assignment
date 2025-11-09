package com.accenture.assessment.service;

import com.accenture.assessment.model.PublicHoliday;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holiday Provider that adds Caching layer to another provider.
 * <p>
 * Thread-safe implementation with LRU (Least Recently Used) eviction policy
 * when the cache reaches its maximum size.
 */
public class CachedHolidayProvider implements HolidayProvider {

    private static final int DEFAULT_MAX_CACHE_SIZE = 100;

    private final HolidayProvider underlyingProvider;
    private final Map<String, List<PublicHoliday>> cache;
    private final int maxCacheSize;

    public CachedHolidayProvider(HolidayProvider underlyingProvider) {
        this(underlyingProvider, DEFAULT_MAX_CACHE_SIZE);
    }

    public CachedHolidayProvider(HolidayProvider underlyingProvider, int maxCacheSize) {
        this.underlyingProvider = underlyingProvider;
        this.maxCacheSize = maxCacheSize;
        this.cache = createLruCache(maxCacheSize);
    }

    @Override
    public List<PublicHoliday> getPublicHolidays(int year, String countryCode) {
        String cacheKey = buildCacheKey(year, countryCode);

        synchronized (cache) {
            List<PublicHoliday> cachedResult = cache.get(cacheKey);
            if (cachedResult != null) {
                return cachedResult;
            }

            List<PublicHoliday> result = underlyingProvider.getPublicHolidays(year, countryCode);
            cache.put(cacheKey, result);
            return result;
        }
    }

    private String buildCacheKey(int year, String countryCode) {
        return year + ":" + countryCode;
    }

    private Map<String, List<PublicHoliday>> createLruCache(int maxSize) {
        return new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, List<PublicHoliday>> eldest) {
                return size() > maxSize;
            }
        };
    }
}
