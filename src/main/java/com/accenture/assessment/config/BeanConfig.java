package com.accenture.assessment.config;

import com.accenture.assessment.service.CachedHolidayProvider;
import com.accenture.assessment.service.NagerHolidayProvider;
import com.accenture.assessment.service.HolidayProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public HolidayProvider holidayProvider(NagerHolidayProvider nagerHolidayProvider) {
        return new CachedHolidayProvider(nagerHolidayProvider);
    }
}
