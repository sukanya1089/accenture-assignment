package com.accenture.assessment.service;

import com.accenture.assessment.model.CountryHolidayCount;
import com.accenture.assessment.model.PublicHoliday;
import com.accenture.assessment.model.SharedHoliday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for processing public holiday data.
 */
@Service
public class HolidayService {

    private final HolidayProvider holidayProvider;

    @Autowired
    public HolidayService(HolidayProvider holidayProvider) {
        this.holidayProvider = holidayProvider;
    }

    /**
     * Retrieves the last 3 celebrated holidays for a given country.
     * Returns holidays that have already passed relative to today's date.
     *
     * @param countryCode the ISO 3166-1 alpha-2 country code
     * @return list of the last 3 celebrated holidays
     */
    public List<PublicHoliday> getLastCelebratedHolidays(String countryCode) {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        // Collect holidays from current and previous years
        List<PublicHoliday> allHolidays = new ArrayList<>();

        // Get holidays from current year and previous year to ensure we have enough past holidays
        for (int year = currentYear; year >= currentYear - 1; year--) {
            List<PublicHoliday> yearHolidays = holidayProvider.getPublicHolidays(year, countryCode);
            if (yearHolidays != null) {
                allHolidays.addAll(yearHolidays);
            }
        }

        // Filter holidays that have already passed and sort by date descending
        return allHolidays.stream()
            .filter(holiday -> holiday.getDate().isBefore(today))
            .sorted(Comparator.comparing(PublicHoliday::getDate).reversed())
            .limit(3)
            .collect(Collectors.toList());
    }

    /**
     * For each given country, returns the number of public holidays not falling on weekends.
     * Results are sorted in descending order by holiday count.
     *
     * @param year the year to check
     * @param countryCodes list of country codes
     * @return list of country holiday counts sorted in descending order
     */
    public List<CountryHolidayCount> getNonWeekendHolidayCount(int year, List<String> countryCodes) {
        return countryCodes.stream()
            .map(countryCode -> {
                List<PublicHoliday> holidays = holidayProvider.getPublicHolidays(year, countryCode);

                if (holidays == null) {
                    return new CountryHolidayCount(countryCode, 0);
                }

                long nonWeekendCount = holidays.stream()
                    .filter(this::isWeekday)
                    .count();

                return new CountryHolidayCount(countryCode, (int) nonWeekendCount);
            })
            .sorted() // Uses the Comparable implementation in CountryHolidayCount
            .collect(Collectors.toList());
    }

    /**
     * Returns the deduplicated list of dates celebrated in both countries with their local names.
     *
     * @param year the year to check
     * @param countryCode1 first country code
     * @param countryCode2 second country code
     * @return list of shared holidays
     */
    public List<SharedHoliday> getSharedHolidays(int year, String countryCode1, String countryCode2) {
        List<PublicHoliday> holidays1 = holidayProvider.getPublicHolidays(year, countryCode1);
        List<PublicHoliday> holidays2 = holidayProvider.getPublicHolidays(year, countryCode2);

        if (holidays1 == null || holidays2 == null) {
            return Collections.emptyList();
        }

        // Create a map of dates to holidays for the first country
        Map<LocalDate, PublicHoliday> holidayMap1 = holidays1.stream()
            .collect(Collectors.toMap(
                PublicHoliday::getDate,
                h -> h,
                (h1, h2) -> h1 // In case of duplicates, keep the first
            ));

        // Find shared dates and create SharedHoliday objects
        Map<LocalDate, SharedHoliday> sharedHolidayMap = new HashMap<>();

        for (PublicHoliday holiday2 : holidays2) {
            LocalDate date = holiday2.getDate();
            if (holidayMap1.containsKey(date)) {
                SharedHoliday sharedHoliday = sharedHolidayMap.computeIfAbsent(
                    date,
                    SharedHoliday::new
                );

                // Add local names from both countries
                PublicHoliday holiday1 = holidayMap1.get(date);
                sharedHoliday.addLocalName(countryCode1, holiday1.getLocalName());
                sharedHoliday.addLocalName(countryCode2, holiday2.getLocalName());
            }
        }

        // Return sorted by date
        return sharedHolidayMap.values().stream()
            .sorted(Comparator.comparing(SharedHoliday::getDate))
            .collect(Collectors.toList());
    }

    /**
     * Checks if a holiday falls on a weekday (Monday-Friday).
     *
     * @param holiday the holiday to check
     * @return true if the holiday is on a weekday
     */
    private boolean isWeekday(PublicHoliday holiday) {
        DayOfWeek dayOfWeek = holiday.getDate().getDayOfWeek();
        // TODO: Islamic countries may have different holidays, need to check this
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }
}
