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

### Option 1: Using Maven Wrapper

The project includes Maven wrapper scripts, so you don't need to have Maven installed.

**On Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**On Windows:**
```cmd
mvnw.cmd spring-boot:run
```

### Option 2: Build and Run JAR

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


## REST API Endpoints

The application exposes REST API endpoints that can be accessed at `http://localhost:8080/api/holidays`.

### API Examples with curl

#### 1. Get Last 3 Celebrated Holidays

Get the last 3 celebrated holidays for a specific country:

```bash
curl http://localhost:8080/api/holidays/last-celebrated/US
```

Try with different countries:
```bash
# United Kingdom
curl http://localhost:8080/api/holidays/last-celebrated/GB

# Germany
curl http://localhost:8080/api/holidays/last-celebrated/DE

# France
curl http://localhost:8080/api/holidays/last-celebrated/FR
```

#### 2. Get Non-Weekend Holiday Count

Get the count of holidays not falling on weekends for multiple countries:

```bash
curl "http://localhost:8080/api/holidays/non-weekend-count?year=2024&countries=US,GB,FR"
```

#### 3. Get Shared Holidays Between Two Countries

Get holidays celebrated on the same dates by two countries:

```bash
curl "http://localhost:8080/api/holidays/shared?year=2024&country1=US&country2=GB"
```

#### 4. Health Check

Verify the API is running:

```bash
curl http://localhost:8080/api/holidays/health
```


## Running Tests

Run all unit tests:

```bash
./mvnw test
```

