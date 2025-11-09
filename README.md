# Holiday Information Retrieval System

Java application that retrieves and processes public holiday information from the [Nager.Date API](https://date.nager.at/Api).

## Features

The application provides three main functionalities:

1. **Last Celebrated Holidays**: Given a country code, retrieve the last 3 celebrated holidays (date and name)
2. **Non-Weekend Holiday Count**: Given a year and country codes, return the number of public holidays not falling on weekends for each country (sorted in descending order)
3. **Shared Holidays**: Given a year and 2 country codes, return the deduplicated list of dates celebrated in both countries with their local names

## Technical Stack

- **Java**: 17
- **Spring Boot**: 3.5.6
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## Prerequisites

- Java 11 or higher (Java 17 recommended)
- Maven 3.6+ (or use the included Maven wrapper)
- Internet connection (to access the Nager.Date API)

## How to Run the Application

### Option 1: Using Maven Wrapper (Recommended)

The project includes Maven wrapper scripts, so you don't need to have Maven installed.

**On Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**On Windows:**
```cmd
mvnw.cmd spring-boot:run
```

### Option 2: Using Installed Maven

If you have Maven installed:

```bash
mvn spring-boot:run
```

### Option 3: Build and Run JAR

1. Build the application:
   ```bash
   ./mvnw clean package
   ```

2. Run the JAR file:
   ```bash
   java -jar target/assessment-0.0.1-SNAPSHOT.jar
   ```

## Using the Application

Once the application starts, you'll see an interactive menu:

```
========================================
Holiday Information Retrieval System
========================================

Please select an option:
1. Get last 3 celebrated holidays for a country
2. Get non-weekend holiday count for countries
3. Get shared holidays between two countries
4. Exit
```

### Example Usage

#### 1. Last Celebrated Holidays

```
Enter your choice (1-4): 1
Enter country code (e.g., US, GB, DE): US

Last 3 celebrated holidays:
----------------------------------------
1. 2024-07-04 - Independence Day (Independence Day)
2. 2024-05-27 - Memorial Day (Memorial Day)
3. 2024-01-01 - New Year's Day (New Year's Day)
```

#### 2. Non-Weekend Holiday Count

```
Enter your choice (1-4): 2
Enter year: 2024
Enter country codes separated by commas (e.g., US,GB,DE): US,GB,DE

Non-weekend holiday count (sorted descending):
----------------------------------------
1. DE: 13 holidays
2. GB: 8 holidays
3. US: 10 holidays
```

#### 3. Shared Holidays

```
Enter your choice (1-4): 3
Enter year: 2024
Enter first country code: US
Enter second country code: GB

Shared holidays:
----------------------------------------
1. 2024-01-01: US - New Year's Day, GB - New Year's Day
2. 2024-12-25: US - Christmas Day, GB - Christmas Day
```

## Running Tests

Run all unit tests:

```bash
./mvnw test
```

Run tests with detailed output:

```bash
./mvnw test -Dtest=HolidayServiceTest
```

Generate test coverage report (if configured):

```bash
./mvnw clean test jacoco:report
```

