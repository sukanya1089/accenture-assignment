package com.accenture.assessment;

import com.accenture.assessment.model.CountryHolidayCount;
import com.accenture.assessment.model.PublicHoliday;
import com.accenture.assessment.model.SharedHoliday;
import com.accenture.assessment.service.HolidayService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Main Spring Boot application for the Holiday Information Retrieval System.
 */
@SpringBootApplication
public class HolidayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(HolidayService holidayService) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            System.out.println("========================================");
            System.out.println("Holiday Information Retrieval System");
            System.out.println("========================================");
            System.out.println();

            while (running) {
                System.out.println("\nPlease select an option:");
                System.out.println("1. Get last 3 celebrated holidays for a country");
                System.out.println("2. Get non-weekend holiday count for countries");
                System.out.println("3. Get shared holidays between two countries");
                System.out.println("4. Exit");
                System.out.print("\nEnter your choice (1-4): ");

                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {
                        case "1":
                            handleLastCelebratedHolidays(scanner, holidayService);
                            break;
                        case "2":
                            handleNonWeekendHolidayCount(scanner, holidayService);
                            break;
                        case "3":
                            handleSharedHolidays(scanner, holidayService);
                            break;
                        case "4":
                            running = false;
                            System.out.println("\nThank you for using the Holiday Information System!");
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                    }
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                    System.out.println("Please try again with valid input.");
                }
            }
        };
    }

    /**
     * Handles the last celebrated holidays query.
     */
    private void handleLastCelebratedHolidays(Scanner scanner, HolidayService holidayService) {
        System.out.print("\nEnter country code (e.g., US, GB, DE): ");
        String countryCode = scanner.nextLine().trim().toUpperCase();

        System.out.println("\nFetching last 3 celebrated holidays for " + countryCode + "...");

        List<PublicHoliday> holidays = holidayService.getLastCelebratedHolidays(countryCode);

        if (holidays.isEmpty()) {
            System.out.println("No celebrated holidays found for " + countryCode);
        } else {
            System.out.println("\nLast " + holidays.size() + " celebrated holidays:");
            System.out.println("----------------------------------------");
            for (int i = 0; i < holidays.size(); i++) {
                PublicHoliday holiday = holidays.get(i);
                System.out.printf("%d. %s - %s (%s)%n",
                    i + 1,
                    holiday.getDate(),
                    holiday.getName(),
                    holiday.getLocalName()
                );
            }
        }
    }

    /**
     * Handles the non-weekend holiday count query.
     */
    private void handleNonWeekendHolidayCount(Scanner scanner, HolidayService holidayService) {
        System.out.print("\nEnter year: ");
        int year = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter country codes separated by commas (e.g., US,GB,DE): ");
        String countriesInput = scanner.nextLine().trim();

        List<String> countryCodes = Arrays.stream(countriesInput.split(","))
            .map(String::trim)
            .map(String::toUpperCase)
            .toList();

        System.out.println("\nFetching non-weekend holidays for " + year + "...");

        List<CountryHolidayCount> counts = holidayService.getNonWeekendHolidayCount(year, countryCodes);

        System.out.println("\nNon-weekend holiday count (sorted descending):");
        System.out.println("----------------------------------------");
        for (int i = 0; i < counts.size(); i++) {
            CountryHolidayCount count = counts.get(i);
            System.out.printf("%d. %s%n", i + 1, count);
        }
    }

    /**
     * Handles the shared holidays query.
     */
    private void handleSharedHolidays(Scanner scanner, HolidayService holidayService) {
        System.out.print("\nEnter year: ");
        int year = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter first country code: ");
        String country1 = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter second country code: ");
        String country2 = scanner.nextLine().trim().toUpperCase();

        System.out.println("\nFetching shared holidays between " + country1 + " and " + country2 + "...");

        List<SharedHoliday> sharedHolidays = holidayService.getSharedHolidays(year, country1, country2);

        if (sharedHolidays.isEmpty()) {
            System.out.println("No shared holidays found between " + country1 + " and " + country2 + " in " + year);
        } else {
            System.out.println("\nShared holidays:");
            System.out.println("----------------------------------------");
            for (int i = 0; i < sharedHolidays.size(); i++) {
                SharedHoliday holiday = sharedHolidays.get(i);
                System.out.printf("%d. %s%n", i + 1, holiday);
            }
        }
    }
}
