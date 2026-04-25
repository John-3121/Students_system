# JDBC Student Record System

## Description

A Java JDBC project that connects to a MySQL database to manage student records. The system supports user authentication with two roles (Admin and Normal User), and includes both a command-line interface and a Java Swing GUI.

## Features

- User sign up and sign in with role-based access (Admin / Normal User)
- Add a student
- View all students
- Remove a student
- Update student info
- Search student by name
- Java Swing GUI with a student table, dialogs, and logout support

## Technologies Used

- Java
- Java Swing (GUI)
- JDBC
- MySQL

## Project Structure

```
├── dbconnection.java       # Database connection
├── student.java            # Student model
├── studentsDAO.java        # All database operations
├── User.java               # Abstract base user class
├── Admin.java              # Admin role (CLI)
├── Normal_user.java        # Normal user role (CLI)
├── main.java               # CLI entry point
├── LoginGUI.java           # GUI entry point — login & sign up
├── AdminGUI.java           # Admin dashboard (GUI)
└── NormalUserGUI.java      # Normal user dashboard (GUI)
```

## Database Setup

1. Clone the repository
2. Create a MySQL database named `students_database`
3. Create the required tables:

```sql
CREATE TABLE Admin (
  id       INT AUTO_INCREMENT PRIMARY KEY,
  email    VARCHAR(100),
  password VARCHAR(100),
  roles    VARCHAR(50)
);

CREATE TABLE students (
  id      INT AUTO_INCREMENT PRIMARY KEY,
  name    VARCHAR(100),
  age     INT,
  course  VARCHAR(100),
  user_id INT
);
```

4. Update the database credentials in `dbconnection.java`:

```java
private static final String url      = "jdbc:mysql://localhost:3306/students_database";
private static final String uname    = "your_username";
private static final String password = "your_password";
```

5. Add the MySQL Connector/J JAR to your classpath.

## Running the Project

**GUI (recommended):**
```bash
javac -cp .:mysql-connector-java.jar *.java
java  -cp .:mysql-connector-java.jar LoginGUI
```

**CLI:**
```bash
javac -cp .:mysql-connector-java.jar *.java
java  -cp .:mysql-connector-java.jar main
```

> On Windows, replace `:` with `;` in the classpath.

## Notes

- Database credentials are not included for security reasons.
- Each user only sees and manages the students they added (filtered by `user_id`).
- The GUI and CLI share the same DAO layer — no logic is duplicated.
