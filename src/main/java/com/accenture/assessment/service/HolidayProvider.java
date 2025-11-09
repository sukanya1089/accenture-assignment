package com.accenture.assessment.service;

import com.accenture.assessment.model.PublicHoliday;

import java.util.List;

/**
 * Interface for fetching Holidays from different providers.
 */
public interface HolidayProvider {

    /**
     * Retrieves all public holidays for a specific year and country.
     *
     * @param year the year
     * @param countryCode the ISO 3166-1 alpha-2 country code
     * @return list of public holidays
     */
    List<PublicHoliday> getPublicHolidays(int year, String countryCode);
}
