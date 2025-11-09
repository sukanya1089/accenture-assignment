package com.accenture.assessment.service;

import com.accenture.assessment.model.PublicHoliday;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Holiday Provider using Nager.Date public holidays API.
 */
@Component
public class NagerHolidayProvider implements HolidayProvider {

    private static final String BASE_URL = "https://date.nager.at/api/v3";
    private final RestTemplate restTemplate;

    public NagerHolidayProvider() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<PublicHoliday> getPublicHolidays(int year, String countryCode) {
        String url = String.format("%s/PublicHolidays/%d/%s", BASE_URL, year, countryCode);

        ResponseEntity<List<PublicHoliday>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }
}
