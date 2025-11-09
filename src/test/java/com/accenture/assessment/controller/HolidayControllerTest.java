package com.accenture.assessment.controller;

import com.accenture.assessment.model.CountryHolidayCount;
import com.accenture.assessment.model.PublicHoliday;
import com.accenture.assessment.model.SharedHoliday;
import com.accenture.assessment.service.HolidayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for HolidayController.
 */
@WebMvcTest(HolidayController.class)
class HolidayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HolidayService holidayService;

    @Test
    void testGetLastCelebratedHolidays_Success() throws Exception {
        // Arrange
        PublicHoliday holiday1 = createHoliday(LocalDate.of(2024, 12, 25), "Christmas", "US");
        PublicHoliday holiday2 = createHoliday(LocalDate.of(2024, 11, 28), "Thanksgiving", "US");
        List<PublicHoliday> holidays = Arrays.asList(holiday1, holiday2);

        when(holidayService.getLastCelebratedHolidays("US")).thenReturn(holidays);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/last-celebrated/US"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Christmas")))
                .andExpect(jsonPath("$[0].countryCode", is("US")))
                .andExpect(jsonPath("$[1].name", is("Thanksgiving")));

        verify(holidayService, times(1)).getLastCelebratedHolidays("US");
    }

    @Test
    void testGetLastCelebratedHolidays_LowerCase() throws Exception {
        // Arrange
        PublicHoliday holiday = createHoliday(LocalDate.of(2024, 12, 25), "Christmas", "GB");
        List<PublicHoliday> holidays = Collections.singletonList(holiday);

        when(holidayService.getLastCelebratedHolidays("GB")).thenReturn(holidays);

        // Act & Assert - Test with lowercase input
        mockMvc.perform(get("/api/holidays/last-celebrated/gb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(holidayService, times(1)).getLastCelebratedHolidays("GB");
    }

    @Test
    void testGetLastCelebratedHolidays_EmptyResult() throws Exception {
        // Arrange
        when(holidayService.getLastCelebratedHolidays("XX")).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/holidays/last-celebrated/XX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetNonWeekendHolidayCount_Success() throws Exception {
        // Arrange
        CountryHolidayCount count1 = new CountryHolidayCount("US", 10);
        CountryHolidayCount count2 = new CountryHolidayCount("GB", 8);
        List<CountryHolidayCount> counts = Arrays.asList(count1, count2);

        when(holidayService.getNonWeekendHolidayCount(eq(2024), any(List.class)))
                .thenReturn(counts);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/non-weekend-count")
                        .param("year", "2024")
                        .param("countries", "US", "GB"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].countryCode", is("US")))
                .andExpect(jsonPath("$[0].holidayCount", is(10)))
                .andExpect(jsonPath("$[1].countryCode", is("GB")))
                .andExpect(jsonPath("$[1].holidayCount", is(8)));

        verify(holidayService, times(1)).getNonWeekendHolidayCount(eq(2024), any(List.class));
    }

    @Test
    void testGetNonWeekendHolidayCount_InvalidYear() throws Exception {
        // Act & Assert - Year too low
        mockMvc.perform(get("/api/holidays/non-weekend-count")
                        .param("year", "1800")
                        .param("countries", "US"))
                .andExpect(status().isBadRequest());

        // Year too high
        mockMvc.perform(get("/api/holidays/non-weekend-count")
                        .param("year", "2200")
                        .param("countries", "US"))
                .andExpect(status().isBadRequest());

        verify(holidayService, never()).getNonWeekendHolidayCount(anyInt(), any());
    }

    @Test
    void testGetNonWeekendHolidayCount_MissingYear() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/holidays/non-weekend-count")
                        .param("countries", "US"))
                .andExpect(status().isBadRequest());

        verify(holidayService, never()).getNonWeekendHolidayCount(anyInt(), any());
    }

    @Test
    void testGetNonWeekendHolidayCount_MissingCountries() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/holidays/non-weekend-count")
                        .param("year", "2024"))
                .andExpect(status().isBadRequest());

        verify(holidayService, never()).getNonWeekendHolidayCount(anyInt(), any());
    }

    @Test
    void testGetSharedHolidays_Success() throws Exception {
        // Arrange
        SharedHoliday shared1 = new SharedHoliday(LocalDate.of(2024, 1, 1));
        shared1.addLocalName("US", "New Year's Day");
        shared1.addLocalName("GB", "New Year's Day");

        SharedHoliday shared2 = new SharedHoliday(LocalDate.of(2024, 12, 25));
        shared2.addLocalName("US", "Christmas Day");
        shared2.addLocalName("GB", "Christmas Day");

        List<SharedHoliday> sharedHolidays = Arrays.asList(shared1, shared2);

        when(holidayService.getSharedHolidays(2024, "US", "GB"))
                .thenReturn(sharedHolidays);

        // Act & Assert
        mockMvc.perform(get("/api/holidays/shared")
                        .param("year", "2024")
                        .param("country1", "US")
                        .param("country2", "GB"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].date", is("2024-01-01")))
                .andExpect(jsonPath("$[0].localNames.US", is("New Year's Day")))
                .andExpect(jsonPath("$[0].localNames.GB", is("New Year's Day")))
                .andExpect(jsonPath("$[1].date", is("2024-12-25")));

        verify(holidayService, times(1)).getSharedHolidays(2024, "US", "GB");
    }

    @Test
    void testGetSharedHolidays_InvalidYear() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/holidays/shared")
                        .param("year", "1800")
                        .param("country1", "US")
                        .param("country2", "GB"))
                .andExpect(status().isBadRequest());

        verify(holidayService, never()).getSharedHolidays(anyInt(), anyString(), anyString());
    }

    @Test
    void testGetSharedHolidays_MissingParameter() throws Exception {
        // Act & Assert - Missing country1
        mockMvc.perform(get("/api/holidays/shared")
                        .param("year", "2024")
                        .param("country2", "GB"))
                .andExpect(status().isBadRequest());

        // Missing country2
        mockMvc.perform(get("/api/holidays/shared")
                        .param("year", "2024")
                        .param("country1", "US"))
                .andExpect(status().isBadRequest());

        verify(holidayService, never()).getSharedHolidays(anyInt(), anyString(), anyString());
    }

    @Test
    void testGetSharedHolidays_NoSharedHolidays() throws Exception {
        // Arrange
        when(holidayService.getSharedHolidays(2024, "US", "FR"))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/holidays/shared")
                        .param("year", "2024")
                        .param("country1", "US")
                        .param("country2", "FR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testHealthEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/holidays/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Holiday API is running"));
    }

    // Helper method
    private PublicHoliday createHoliday(LocalDate date, String name, String countryCode) {
        PublicHoliday holiday = new PublicHoliday();
        holiday.setDate(date);
        holiday.setName(name);
        holiday.setLocalName(name);
        holiday.setCountryCode(countryCode);
        return holiday;
    }
}
