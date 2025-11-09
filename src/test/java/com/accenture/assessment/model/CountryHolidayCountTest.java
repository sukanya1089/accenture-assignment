package com.accenture.assessment.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CountryHolidayCount.
 */
class CountryHolidayCountTest {

    @Test
    void testCompareTo_SortsInDescendingOrder() {
        // Arrange
        CountryHolidayCount count1 = new CountryHolidayCount("US", 10);
        CountryHolidayCount count2 = new CountryHolidayCount("GB", 8);
        CountryHolidayCount count3 = new CountryHolidayCount("FR", 12);

        List<CountryHolidayCount> counts = Arrays.asList(count1, count2, count3);

        // Act
        Collections.sort(counts);

        // Assert
        assertEquals("FR", counts.get(0).getCountryCode()); // 12 holidays
        assertEquals("US", counts.get(1).getCountryCode()); // 10 holidays
        assertEquals("GB", counts.get(2).getCountryCode()); // 8 holidays
    }

    @Test
    void testToString() {
        // Arrange
        CountryHolidayCount count = new CountryHolidayCount("US", 10);

        // Act
        String result = count.toString();

        // Assert
        assertEquals("US: 10 holidays", result);
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        CountryHolidayCount count = new CountryHolidayCount("US", 5);

        // Act
        count.setCountryCode("GB");
        count.setHolidayCount(7);

        // Assert
        assertEquals("GB", count.getCountryCode());
        assertEquals(7, count.getHolidayCount());
    }
}
