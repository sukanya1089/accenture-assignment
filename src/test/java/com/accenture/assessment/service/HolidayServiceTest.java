package com.accenture.assessment.service;

import com.accenture.assessment.model.CountryHolidayCount;
import com.accenture.assessment.model.PublicHoliday;
import com.accenture.assessment.model.SharedHoliday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HolidayService.
 */
@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayProvider holidayProvider;

    private HolidayService holidayService;

    @BeforeEach
    void setUp() {
        holidayService = new HolidayService(holidayProvider);
    }

    @Test
    void testGetLastCelebratedHolidays() {
        // Arrange
        String countryCode = "US";
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        // Create test holidays - mix of past and future
        List<PublicHoliday> currentYearHolidays = Arrays.asList(
            createHoliday(LocalDate.of(currentYear, 1, 1), "New Year's Day", "New Year's Day", countryCode),
            createHoliday(LocalDate.of(currentYear, 7, 4), "Independence Day", "Independence Day", countryCode),
            createHoliday(LocalDate.of(currentYear, 12, 25), "Christmas Day", "Christmas Day", countryCode)
        );

        List<PublicHoliday> previousYearHolidays = Arrays.asList(
            createHoliday(LocalDate.of(currentYear - 1, 1, 1), "New Year's Day", "New Year's Day", countryCode),
            createHoliday(LocalDate.of(currentYear - 1, 12, 25), "Christmas Day", "Christmas Day", countryCode)
        );

        when(holidayProvider.getPublicHolidays(currentYear, countryCode)).thenReturn(currentYearHolidays);
        when(holidayProvider.getPublicHolidays(currentYear - 1, countryCode)).thenReturn(previousYearHolidays);

        // Act
        List<PublicHoliday> result = holidayService.getLastCelebratedHolidays(countryCode);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() <= 3);
        // All returned holidays should be in the past
        for (PublicHoliday holiday : result) {
            assertTrue(holiday.getDate().isBefore(today));
        }
        // Should be sorted in descending order (most recent first)
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getDate().isAfter(result.get(i + 1).getDate()));
        }
    }

    @Test
    void testGetNonWeekendHolidayCount() {
        // Arrange
        int year = 2024;
        List<String> countryCodes = Arrays.asList("US", "GB");

        // US holidays - mix of weekdays and weekends
        List<PublicHoliday> usHolidays = Arrays.asList(
            createHoliday(LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day", "US"), // Monday
            createHoliday(LocalDate.of(2024, 7, 4), "Independence Day", "Independence Day", "US"), // Thursday
            createHoliday(LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day", "US") // Wednesday
        );

        // GB holidays
        List<PublicHoliday> gbHolidays = Arrays.asList(
            createHoliday(LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day", "GB"), // Monday
            createHoliday(LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day", "GB") // Wednesday
        );

        when(holidayProvider.getPublicHolidays(year, "US")).thenReturn(usHolidays);
        when(holidayProvider.getPublicHolidays(year, "GB")).thenReturn(gbHolidays);

        // Act
        List<CountryHolidayCount> result = holidayService.getNonWeekendHolidayCount(year, countryCodes);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Should be sorted in descending order
        assertEquals("US", result.get(0).getCountryCode());
        assertEquals(3, result.get(0).getHolidayCount());
        assertEquals("GB", result.get(1).getCountryCode());
        assertEquals(2, result.get(1).getHolidayCount());
    }

    @Test
    void testGetNonWeekendHolidayCountFiltersWeekends() {
        // Arrange
        int year = 2024;
        List<String> countryCodes = Arrays.asList("US");

        // Create holidays on specific days including weekends
        List<PublicHoliday> holidays = Arrays.asList(
            createHoliday(LocalDate.of(2024, 11, 2), "Saturday Holiday", "Saturday Holiday", "US"), // Saturday
            createHoliday(LocalDate.of(2024, 11, 3), "Sunday Holiday", "Sunday Holiday", "US"), // Sunday
            createHoliday(LocalDate.of(2024, 11, 4), "Monday Holiday", "Monday Holiday", "US")  // Monday
        );

        when(holidayProvider.getPublicHolidays(year, "US")).thenReturn(holidays);

        // Act
        List<CountryHolidayCount> result = holidayService.getNonWeekendHolidayCount(year, countryCodes);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getHolidayCount()); // Only Monday should count
    }

    @Test
    void testGetSharedHolidays() {
        // Arrange
        int year = 2024;
        String country1 = "US";
        String country2 = "GB";

        List<PublicHoliday> usHolidays = Arrays.asList(
            createHoliday(LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day", "US"),
            createHoliday(LocalDate.of(2024, 7, 4), "Independence Day", "Independence Day", "US"),
            createHoliday(LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day", "US")
        );

        List<PublicHoliday> gbHolidays = Arrays.asList(
            createHoliday(LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day", "GB"),
            createHoliday(LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day", "GB"),
            createHoliday(LocalDate.of(2024, 12, 26), "Boxing Day", "Boxing Day", "GB")
        );

        when(holidayProvider.getPublicHolidays(year, country1)).thenReturn(usHolidays);
        when(holidayProvider.getPublicHolidays(year, country2)).thenReturn(gbHolidays);

        // Act
        List<SharedHoliday> result = holidayService.getSharedHolidays(year, country1, country2);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // New Year and Christmas

        // Check first shared holiday
        SharedHoliday newYear = result.get(0);
        assertEquals(LocalDate.of(2024, 1, 1), newYear.getDate());
        assertEquals(2, newYear.getLocalNames().size());
        assertTrue(newYear.getLocalNames().containsKey("US"));
        assertTrue(newYear.getLocalNames().containsKey("GB"));

        // Check second shared holiday
        SharedHoliday christmas = result.get(1);
        assertEquals(LocalDate.of(2024, 12, 25), christmas.getDate());
        assertEquals(2, christmas.getLocalNames().size());
    }

    @Test
    void testGetSharedHolidaysWithNoSharedDates() {
        // Arrange
        int year = 2024;
        String country1 = "US";
        String country2 = "FR";

        List<PublicHoliday> usHolidays = Arrays.asList(
            createHoliday(LocalDate.of(2024, 7, 4), "Independence Day", "Independence Day", "US")
        );

        List<PublicHoliday> frHolidays = Arrays.asList(
            createHoliday(LocalDate.of(2024, 7, 14), "Bastille Day", "Fête nationale", "FR")
        );

        when(holidayProvider.getPublicHolidays(year, country1)).thenReturn(usHolidays);
        when(holidayProvider.getPublicHolidays(year, country2)).thenReturn(frHolidays);

        // Act
        List<SharedHoliday> result = holidayService.getSharedHolidays(year, country1, country2);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNonWeekendHolidayCountWithNullResponse() {
        // Arrange
        int year = 2024;
        List<String> countryCodes = Arrays.asList("INVALID");

        when(holidayProvider.getPublicHolidays(year, "INVALID")).thenReturn(null);

        // Act
        List<CountryHolidayCount> result = holidayService.getNonWeekendHolidayCount(year, countryCodes);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getHolidayCount());
    }

    // Helper method to create a PublicHoliday for testing
    private PublicHoliday createHoliday(LocalDate date, String name, String localName, String countryCode) {
        PublicHoliday holiday = new PublicHoliday();
        holiday.setDate(date);
        holiday.setName(name);
        holiday.setLocalName(localName);
        holiday.setCountryCode(countryCode);
        return holiday;
    }
}
