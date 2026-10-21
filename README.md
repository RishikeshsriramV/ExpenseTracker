# Expense Tracker (Java Servlet + JSP + MySQL)

A web app to track personal expenses. Users register, log in, add / edit / delete expenses,
filter by month and category, and see a monthly dashboard with a category breakdown.

## Tech stack
- Java 21+ (tested with JDK 27), Jakarta Servlet 6.1, JSP + JSTL
- Apache Tomcat 11
- MySQL 8 + JDBC (PreparedStatements, DAO pattern)
- Maven (WAR packaging), Bootstrap 5 (CDN)
- BCrypt password hashing, session login, servlet filter for authentication

## Features
- Register / login / logout (BCrypt hashed passwords, session fixation protection)
- Add, edit, delete expenses (each user only sees their own data)
- Filter expenses by month and category, with running total
- Dashboard: this month total, all-time total, spending by category, recent expenses
- Server-side validation, XSS-safe output (`<c:out>`), SQL injection safe (PreparedStatement)

## Project structure
```
src/main/java/com/expensetracker
  model/    User, Expense, CategoryTotal
  dao/      UserDAO, ExpenseDAO        (all SQL lives here)
  servlet/  Register, Login, Logout, Dashboard, Expense servlets (controllers)
  filter/   AuthFilter, EncodingFilter
  util/     DBUtil, Categories
src/main/resources/db.properties       (DB url / user / password)
src/main/webapp/WEB-INF/views/*.jsp    (views)
database/schema.sql                    (tables)
```

## Setup
1. **Database**: open MySQL Workbench, run `database/schema.sql`.
2. **DB password**: edit `src/main/resources/db.properties` and set `db.password` to your MySQL root password.
3. **Eclipse**: File > Import > Maven > Existing Maven Projects > select this folder > Finish.
   (Wait for Maven to download dependencies. Right click project > Maven > Update Project if needed.)
4. Right click project > Run As > Run on Server > Apache Tomcat v11.0.
5. Open http://localhost:8080/ExpenseTracker/

## Push to GitHub
```
git init
git add .
git commit -m "Expense Tracker: Servlet + JSP + MySQL"
git branch -M main
git remote add origin https://github.com/<your-username>/ExpenseTracker.git
git push -u origin main
```
`db.properties` is in `.gitignore` so your real password is not uploaded.
Others can copy `db.properties.example` to `db.properties` and set their own password.

## Ideas to extend
- Monthly budget with alerts, CSV export, charts (Chart.js), edit profile / change password,
  pagination, connection pooling (HikariCP), unit tests.
