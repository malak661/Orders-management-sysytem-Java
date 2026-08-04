# Orders Management System (Java SE)

Console-based application for managing customers, products, inventory, orders,
payments, and order status transitions.

## Architecture

3-layer architecture:

```
Presentation (app/)  -->  Business Logic (service/)  -->  Data Access (repository/)
```

- **model/** — Entities and enums (Customer, Product, Order, OrderItem, Payment, OrderStatus, PaymentMethod, PaymentStatus)
- **repository/** — Data access interfaces + `impl/` JDBC implementations (SQLite)
- **service/** — Business rules, validation, calculations
- **exception/** — Custom checked/unchecked exceptions for business rule violations
- **util/** — DB connection helper, console input helper, ID generator
- **app/** — Main entry point + console menus (Presentation layer only, no business logic)

## Requirements

- Java 17+ (JDK)
- Maven 3.8+

## Build

```bash
mvn clean package
```

This produces `target/orders-management-system.jar`.

> NOTE: the JDBC SQLite driver is a Maven dependency, not bundled by default.
> TODO: add `maven-shade-plugin` (or `maven-assembly-plugin`) to build a fat-jar
> that includes the driver, otherwise run via `mvn exec:java` or set the classpath manually.

## Run (during development)

```bash
mvn compile exec:java -Dexec.mainClass="com.ordersystem.app.Main"
```

(TODO: add `exec-maven-plugin` to `pom.xml` if using this method)

## Database

SQLite file-based DB (`orders.db`, created automatically on first run).
Schema defined in `src/main/resources/schema.sql`.
TODO: confirm `DbConnection.initializeSchema()` runs this file on startup (AC9).

## Project Status / TODO Tracker

This is the initial project skeleton. All classes contain `// TODO` markers
indicating what needs to be implemented. See task split below.

## Suggested Team Task Split (adjust to team size: 3-4 people)

| Member | Owns |
|---|---|
| **Member A** | `model/` (all entities + enums) + `exception/` package (shared foundation — do first) |
| **Member B** | `repository/` interfaces + `repository/impl/` JDBC implementations + `resources/schema.sql` + `util/DbConnection.java` |
| **Member C** | `service/CustomerService`, `service/ProductService`, `service/OrderService`, `service/OrderStatusValidator` |
| **Member D** | `service/PaymentService`, `service/ReportService` + `app/` (all console menus) + `util/ConsoleInputHelper.java`, `util/IdGenerator.java` |

### Suggested order of work
1. Member A finishes `model/` + `exception/` first — everyone else depends on these.
2. Member B builds `repository/` layer against the finished models.
3. Members C & D build `service/` layer against repository interfaces (can use mocks/stubs before B finishes).
4. Member D wires up `app/` console menus last, once services are ready.
5. Integration: wire everything in `Main.java`.
6. Test manually against AC1–AC12 in the assignment.

## Acceptance Criteria Checklist (AC1–AC12)

- [ ] AC1 - Console CRUD for customers and products
- [ ] AC2 - Reject duplicate emails, negative prices, invalid stock
- [ ] AC3 - Order requires valid customer + at least one valid item
- [ ] AC4 - Reject quantity > stock, no partial order creation
- [ ] AC5 - Confirm reduces stock once; cancel restores it
- [ ] AC6 - BigDecimal-based subtotal/discount/tax/total
- [ ] AC7 - Valid order status transitions only
- [ ] AC8 - Payments capped at remaining balance
- [ ] AC9 - Data persists after restart (SQLite)
- [ ] AC10 - Reports: customer history, low stock, unpaid orders, sales summary
- [ ] AC11 - Clear package separation (model/repository/service/exception/app)
- [ ] AC12 - This README + successful `mvn clean package`
