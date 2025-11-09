package com.accenture.assessment.service;

import com.accenture.assessment.model.PublicHoliday;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holiday Provider that adds Caching layer to another provider.
 * <p>
 * Thread-safe implementation using ConcurrentHashMap.
 */
public class CachedHolidayProvider implements HolidayProvider {

    private final HolidayProvider underlyingProvider;
    private final ConcurrentHashMap<String, List<PublicHoliday>> cache;

    public CachedHolidayProvider(HolidayProvider underlyingProvider) {
        this.underlyingProvider = underlyingProvider;
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public List<PublicHoliday> getPublicHolidays(int year, String countryCode) {
        String cacheKey = buildCacheKey(year, countryCode);

        return cache.computeIfAbsent(cacheKey, key ->
            underlyingProvider.getPublicHolidays(year, countryCode)
        );
    }

    private String buildCacheKey(int year, String countryCode) {
        return year + ":" + countryCode;
    }
}
