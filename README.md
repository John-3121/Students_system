JDBC Student Record System

Description

This is a simple Java JDBC project that connects to a MySQL database and performs basic operations.

Features

- Add student
- View all students
- remove student
- search student
- exit

Technologies Used

- Java
- JDBC
- MySQL

Setup

1. Clone the repository
2. Create a MySQL database named "students_database"
3. Create a table:

CREATE TABLE students (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  age INT,
  course VARCHAR(100)
);

4. Update database credentials in "dbconnection.java"
5. Run the program

Notes

Database credentials are not included for security reasons.
