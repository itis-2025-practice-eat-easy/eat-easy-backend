# EatEasy Backend

This document explains the structure and setup of our Java backend project using Gradle.

## Project Structure
```
eat-easy-backend/
├── README.md - This documentation file
├── build.gradle - Common configuration for all subprojects
├── buildSrc/ - Custom Gradle scripts (future use)
├── modules/ - Shared modules (libraries used by services)
│ └── build.gradle - Configuration for all module subprojects
├── services/ - Microservices (Spring Boot applications)
│ └── build.gradle - Configuration for all service subprojects
└── settings.gradle - Project structure configuration
```

## Prerequisites
- Java 17 or higher
- Gradle 8.0 or higher