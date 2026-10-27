# MiniBank — OOP Semester Project

A Java 17 console banking application demonstrating encapsulation, inheritance, polymorphism, records, enums, interfaces, annotations/reflection, custom checked exceptions, concurrency, serialization/NIO, collections, generics, and JUnit 5.

## Requirements
- JDK 17+
- Maven 3.8+ (or open the folder in IntelliJ IDEA / VS Code with Java extensions)

## Run
```bash
mvn test
mvn package
java -jar target/oop-minibank-1.0.0.jar
```
The application creates `data/` and `logs/` in the working directory. The saved state is `data/accounts.ser`; transaction logs are appended under `logs/`.

## Project layout
- `model`: Customer, Account and account subclasses, records/enums/interfaces
- `model.annotation`: custom runtime validation annotations
- `exception`: checked banking exceptions
- `util`: validation, parsing, statements, persistence and reports
- `repository`: generic reflection-based Repository and AccountStore
- `service`: BankService and transaction processor
- `MiniBank`: console entry point

