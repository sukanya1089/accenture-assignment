package com.accenture.assessment.model;

/**
 * Model class representing the count of non-weekend holidays for a country.
 */
public class CountryHolidayCount implements Comparable<CountryHolidayCount> {

    private String countryCode;
    private int holidayCount;

    public CountryHolidayCount(String countryCode, int holidayCount) {
        this.countryCode = countryCode;
        this.holidayCount = holidayCount;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getHolidayCount() {
        return holidayCount;
    }

    public void setHolidayCount(int holidayCount) {
        this.holidayCount = holidayCount;
    }

    @Override
    public int compareTo(CountryHolidayCount other) {
        // Sort in descending order
        return Integer.compare(other.holidayCount, this.holidayCount);
    }

    @Override
    public String toString() {
        return countryCode + ": " + holidayCount + " holidays";
    }
}
