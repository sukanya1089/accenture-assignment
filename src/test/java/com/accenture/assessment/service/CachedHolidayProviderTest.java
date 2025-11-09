package com.accenture.assessment.service;

import com.accenture.assessment.model.PublicHoliday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CachedHolidayProvider.
 */
@ExtendWith(MockitoExtension.class)
class CachedHolidayProviderTest {

    @Mock
    private HolidayProvider underlyingProvider;

    private CachedHolidayProvider cachedProvider;

    @BeforeEach
    void setUp() {
        cachedProvider = new CachedHolidayProvider(underlyingProvider);
    }

    @Test
    void testCacheMiss_CallsUnderlyingProvider() {
        // Arrange
        int year = 2024;
        String countryCode = "US";
        List<PublicHoliday> expectedHolidays = createTestHolidays(year, countryCode);

        when(underlyingProvider.getPublicHolidays(year, countryCode)).thenReturn(expectedHolidays);

        // Act
        List<PublicHoliday> result = cachedProvider.getPublicHolidays(year, countryCode);

        // Assert
        assertNotNull(result);
        assertEquals(expectedHolidays, result);
        verify(underlyingProvider, times(1)).getPublicHolidays(year, countryCode);
    }

    @Test
    void testCacheHit_DoesNotCallUnderlyingProvider() {
        // Arrange
        int year = 2024;
        String countryCode = "US";
        List<PublicHoliday> expectedHolidays = createTestHolidays(year, countryCode);

        when(underlyingProvider.getPublicHolidays(year, countryCode)).thenReturn(expectedHolidays);

        // Act - First call
        List<PublicHoliday> firstResult = cachedProvider.getPublicHolidays(year, countryCode);

        // Act - Second call (should use cache)
        List<PublicHoliday> secondResult = cachedProvider.getPublicHolidays(year, countryCode);

        // Assert
        assertNotNull(firstResult);
        assertNotNull(secondResult);
        assertSame(firstResult, secondResult); // Should be same instance from cache
        verify(underlyingProvider, times(1)).getPublicHolidays(year, countryCode); // Only called once
    }

    @Test
    void testMultipleCacheKeys_CachedSeparately() {
        // Arrange
        int year1 = 2024;
        String country1 = "US";
        List<PublicHoliday> holidays1 = createTestHolidays(year1, country1);

        int year2 = 2024;
        String country2 = "GB";
        List<PublicHoliday> holidays2 = createTestHolidays(year2, country2);

        when(underlyingProvider.getPublicHolidays(year1, country1)).thenReturn(holidays1);
        when(underlyingProvider.getPublicHolidays(year2, country2)).thenReturn(holidays2);

        // Act
        List<PublicHoliday> result1 = cachedProvider.getPublicHolidays(year1, country1);
        List<PublicHoliday> result2 = cachedProvider.getPublicHolidays(year2, country2);
        List<PublicHoliday> cachedResult1 = cachedProvider.getPublicHolidays(year1, country1);
        List<PublicHoliday> cachedResult2 = cachedProvider.getPublicHolidays(year2, country2);

        // Assert
        assertEquals(holidays1, result1);
        assertEquals(holidays2, result2);
        assertSame(result1, cachedResult1);
        assertSame(result2, cachedResult2);
        verify(underlyingProvider, times(1)).getPublicHolidays(year1, country1);
        verify(underlyingProvider, times(1)).getPublicHolidays(year2, country2);
    }

    @Test
    void testDifferentYearsSameCacheKey_CachedSeparately() {
        // Arrange
        String countryCode = "US";
        int year1 = 2024;
        int year2 = 2025;

        List<PublicHoliday> holidays2024 = createTestHolidays(year1, countryCode);
        List<PublicHoliday> holidays2025 = createTestHolidays(year2, countryCode);

        when(underlyingProvider.getPublicHolidays(year1, countryCode)).thenReturn(holidays2024);
        when(underlyingProvider.getPublicHolidays(year2, countryCode)).thenReturn(holidays2025);

        // Act
        List<PublicHoliday> result2024 = cachedProvider.getPublicHolidays(year1, countryCode);
        List<PublicHoliday> result2025 = cachedProvider.getPublicHolidays(year2, countryCode);
        List<PublicHoliday> cachedResult2024 = cachedProvider.getPublicHolidays(year1, countryCode);

        // Assert
        assertEquals(holidays2024, result2024);
        assertEquals(holidays2025, result2025);
        assertSame(result2024, cachedResult2024);
        verify(underlyingProvider, times(1)).getPublicHolidays(year1, countryCode);
        verify(underlyingProvider, times(1)).getPublicHolidays(year2, countryCode);
    }

    @Test
    void testEmptyList_IsCached() {
        // Arrange
        int year = 2024;
        String countryCode = "XX"; // Non-existent country
        List<PublicHoliday> emptyList = new ArrayList<>();

        when(underlyingProvider.getPublicHolidays(year, countryCode)).thenReturn(emptyList);

        // Act
        List<PublicHoliday> firstResult = cachedProvider.getPublicHolidays(year, countryCode);
        List<PublicHoliday> secondResult = cachedProvider.getPublicHolidays(year, countryCode);

        // Assert
        assertTrue(firstResult.isEmpty());
        assertSame(firstResult, secondResult);
        verify(underlyingProvider, times(1)).getPublicHolidays(year, countryCode);
    }

    @Test
    void testThreadSafety_ConcurrentAccess() throws InterruptedException {
        // Arrange
        int year = 2024;
        String countryCode = "US";
        List<PublicHoliday> expectedHolidays = createTestHolidays(year, countryCode);
        AtomicInteger callCount = new AtomicInteger(0);

        // Simulate a slow underlying provider
        when(underlyingProvider.getPublicHolidays(year, countryCode)).thenAnswer(invocation -> {
            callCount.incrementAndGet();
            Thread.sleep(50); // Simulate network delay
            return expectedHolidays;
        });

        int numThreads = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<List<PublicHoliday>> results = new ArrayList<>();

        // Act - Launch multiple threads simultaneously
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    List<PublicHoliday> result = cachedProvider.getPublicHolidays(year, countryCode);
                    synchronized (results) {
                        results.add(result);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Start all threads
        completionLatch.await(); // Wait for all to complete
        executor.shutdown();

        // Assert
        assertEquals(numThreads, results.size());
        // All threads should get the same cached instance
        for (List<PublicHoliday> result : results) {
            assertSame(expectedHolidays, result);
        }
        // Underlying provider should be called only once due to computeIfAbsent thread safety
        assertEquals(1, callCount.get());
    }

    @Test
    void testThreadSafety_DifferentKeys() throws InterruptedException {
        // Arrange
        List<String> countries = Arrays.asList("US", "GB", "FR", "DE", "IT");
        int year = 2024;

        for (String country : countries) {
            when(underlyingProvider.getPublicHolidays(year, country))
                    .thenReturn(createTestHolidays(year, country));
        }

        int numThreads = 20;
        CountDownLatch completionLatch = new CountDownLatch(numThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Act - Multiple threads requesting different countries
        for (int i = 0; i < numThreads; i++) {
            String country = countries.get(i % countries.size());
            executor.submit(() -> {
                try {
                    cachedProvider.getPublicHolidays(year, country);
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        completionLatch.await();
        executor.shutdown();

        // Assert - Each country should be fetched exactly once
        for (String country : countries) {
            verify(underlyingProvider, times(1)).getPublicHolidays(year, country);
        }
    }

    @Test
    void testNullReturnFromUnderlyingProvider_IsCached() {
        // Arrange
        int year = 2024;
        String countryCode = "XX";

        when(underlyingProvider.getPublicHolidays(year, countryCode)).thenReturn(null);

        // Act
        List<PublicHoliday> firstResult = cachedProvider.getPublicHolidays(year, countryCode);
        List<PublicHoliday> secondResult = cachedProvider.getPublicHolidays(year, countryCode);

        // Assert
        assertNull(firstResult);
        assertNull(secondResult);
        // Null is not inserted into Map/Cache, so its invoked 2 times.
        verify(underlyingProvider, times(2)).getPublicHolidays(year, countryCode);
    }

    // Helper method to create test holidays
    private List<PublicHoliday> createTestHolidays(int year, String countryCode) {
        PublicHoliday holiday1 = new PublicHoliday();
        holiday1.setDate(LocalDate.of(year, 1, 1));
        holiday1.setName("New Year's Day");
        holiday1.setLocalName("New Year's Day");
        holiday1.setCountryCode(countryCode);

        PublicHoliday holiday2 = new PublicHoliday();
        holiday2.setDate(LocalDate.of(year, 12, 25));
        holiday2.setName("Christmas Day");
        holiday2.setLocalName("Christmas Day");
        holiday2.setCountryCode(countryCode);

        return Arrays.asList(holiday1, holiday2);
    }
}
