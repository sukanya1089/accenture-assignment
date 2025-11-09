package com.accenture.assessment.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing a holiday shared between multiple countries.
 */
public class SharedHoliday {

    private LocalDate date;
    private Map<String, String> localNames; // countryCode -> localName

    public SharedHoliday(LocalDate date) {
        this.date = date;
        this.localNames = new HashMap<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, String> getLocalNames() {
        return localNames;
    }

    public void addLocalName(String countryCode, String localName) {
        this.localNames.put(countryCode, localName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(date).append(": ");
        localNames.forEach((country, name) ->
            sb.append(country).append(" - ").append(name).append(", ")
        );
        // Remove trailing comma and space
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}
