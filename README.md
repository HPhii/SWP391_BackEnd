# Koi Feng Shui System
The **Koi Feng Shui System** is a web-based platform designed to serve Koi enthusiasts, those who are new to feng shui Koi can learn about the types of fish that are suitable for them, feng shui can be considered an art, so take your time to explore the Koi Feng Shui system.
## Table of Contents
- [Features](#features)
- [Technologies](#technologies)
- [Setup](#setup)
## Features
- **User Authentication**: Supports JWT-based authentication, with roles such as Customer, Admin, Guest.
- **Role Management**: Assigns default roles on registration, with different permissions for each role.
- **Koi Fish Feng Shui Consultation**: User can get their Fate based on birthdate inputted and receive consultation about Koi, Pond that well suitable with this Fate.
- **Post Advertisment**: Users can also Post some Koi, Pond Service or any Feng Shui Product/ Decoration and Pay for advertising service of system.
- **Google Login**: OAuth2 integration for Google and login.
## Technologies
- **Backend**: Spring Boot 3, Spring Security, Spring Data JPA
- **Frontend**: React
- **Database**: MySQL
- **Authentication**: JWT, OAuth2 (Google)
- **Email**: Spring Mail (Gmail)
## Setup
1. Clone the repository:
    ```bash
    git clone https://github.com/HPhii/SWP391_BackEnd.git
    ```
2. Navigate to the project directory:
    ```bash
    cd SWP391_Backend
    ```
3. Set up the MySQL database:
    - Create a database named `koifengshui`.
    - Configure the database connection in `application.properties`:
      ```properties
      spring.datasource.url=${MYSQL_URL}
      spring.datasource.username=${MYSQL_USER}
      spring.datasource.password=${MYSQL_PASSWORD}
      ```
4. Run the application:
    ```bash
    ./mvnw spring-boot:run
    ```
