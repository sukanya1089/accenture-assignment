package com.accenture.assessment.controller;

import com.accenture.assessment.model.CountryHolidayCount;
import com.accenture.assessment.model.PublicHoliday;
import com.accenture.assessment.model.SharedHoliday;
import com.accenture.assessment.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for holiday-related endpoints.
 */
@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    @Autowired
    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    /**
     * Get the last 3 celebrated holidays for a given country.
     *
     * @param countryCode ISO 3166-1 alpha-2 country code (e.g., US, GB, FR)
     * @return list of last 3 celebrated holidays
     */
    @GetMapping("/last-celebrated/{countryCode}")
    public ResponseEntity<List<PublicHoliday>> getLastCelebratedHolidays(
            @PathVariable String countryCode) {

        if (countryCode == null || countryCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<PublicHoliday> holidays = holidayService.getLastCelebratedHolidays(
                countryCode.toUpperCase());

        return ResponseEntity.ok(holidays);
    }

    /**
     * Get the count of non-weekend holidays for multiple countries in a given year.
     * Results are sorted in descending order by holiday count.
     *
     * @param year the year to query
     * @param countries comma-separated list of country codes (e.g., US,GB,FR)
     * @return list of countries with their non-weekend holiday counts
     */
    @GetMapping("/non-weekend-count")
    public ResponseEntity<List<CountryHolidayCount>> getNonWeekendHolidayCount(
            @RequestParam int year,
            @RequestParam List<String> countries) {

        if (year < 1900 || year > 2100) {
            return ResponseEntity.badRequest().build();
        }

        if (countries == null || countries.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Convert to uppercase for consistency
        List<String> upperCaseCountries = countries.stream()
                .map(String::toUpperCase)
                .toList();

        List<CountryHolidayCount> holidayCounts = holidayService.getNonWeekendHolidayCount(
                year, upperCaseCountries);

        return ResponseEntity.ok(holidayCounts);
    }

    /**
     * Get the shared holidays between two countries for a given year.
     *
     * @param year the year to query
     * @param country1 first country code
     * @param country2 second country code
     * @return list of shared holidays with local names from both countries
     */
    @GetMapping("/shared")
    public ResponseEntity<List<SharedHoliday>> getSharedHolidays(
            @RequestParam int year,
            @RequestParam String country1,
            @RequestParam String country2) {

        if (year < 1900 || year > 2100) {
            return ResponseEntity.badRequest().build();
        }

        if (country1 == null || country1.trim().isEmpty() ||
            country2 == null || country2.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<SharedHoliday> sharedHolidays = holidayService.getSharedHolidays(
                year, country1.toUpperCase(), country2.toUpperCase());

        return ResponseEntity.ok(sharedHolidays);
    }

    /**
     * Health check endpoint for the holidays API.
     *
     * @return simple OK response
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Holiday API is running");
    }
}
