# MiniBank

MiniBank is a console-based banking application developed for the
CHARUSAT CEUE203 Object-Oriented Programming practicals.

## Requirements

- Java JDK 17 or newer
- JUnit 5 for running automated tests
- Ubuntu/Linux or another operating system that supports Java

## Features

### Banking operations
- Open savings and current accounts
- Deposit and withdraw money
- Transfer funds between accounts
- View an account statement
- List all accounts
- Generate an end-of-day transaction report
- Save and reload account data

### Object-oriented programming
- Classes, objects, constructors, and encapsulation
- Inheritance and polymorphism
- Abstract classes and interfaces
- Exception handling
- Records, enums, and switch expressions

### Collections and generics
- Collections and account sorting
- HashMap-based generic repository
- Reflection-based ID lookup
- Generic methods and bounded type parameters

### File handling and concurrency
- Account persistence using serialization
- Transaction logging and report generation
- Concurrent transaction processing
- Thread-safe account balance updates
- Producer-consumer and deadlock-prevention demonstrations

### Validation and testing
- Annotation-based validation
- Input and banking-operation validation
- JUnit 5 tests for deposits, withdrawal limits, and concurrent deposits

## Compile

Run these commands from the project root:

```bash
mkdir -p out
javac -d out exception/*.java model/*.java model/annotation/*.java util/*.java service/*.java app/*.java
```

## Run from compiled classes

```bash
java -cp out app.MiniBank
```

## Build a runnable JAR

```bash
javac -d out exception/*.java model/*.java model/annotation/*.java util/*.java service/*.java app/*.java
jar --create --file minibank.jar --manifest manifest.txt -C out .
```

Run the JAR:

```bash
java -jar minibank.jar
```

## Run JUnit tests on Ubuntu

Install JUnit 5 if it is not already installed:

```bash
sudo apt update
sudo apt install junit5
```

Compile and execute the tests:

```bash
JUNIT=/usr/share/java/junit-platform-console-standalone.jar
mkdir -p test-out

javac -cp "out:$JUNIT" -d test-out test/BankServiceTest.java

java -jar "$JUNIT" execute \
  --class-path "out:test-out" \
  --select-class BankServiceTest \
  --details tree
```

## Data files

- `accounts.dat` stores serialized account data.
- `data/transactions.log` records deposit and withdrawal transactions.
- `data/end-of-day-report.txt` stores the generated report.

These files are created or updated while running the application.
Run the application from the project directory so the data files are
stored in the expected location.

## Project structure

- `app/` - Application entry point and console menu
- `model/` - Accounts, customers, and domain types
- `model/annotation/` - Custom annotations
- `service/` - Banking operations, persistence, repositories, and transactions
- `exception/` - Custom banking exceptions
- `util/` - Validation, sessions, and statement formatting
- `test/` - JUnit tests

## JAR manifest

The `manifest.txt` file specifies the entry point:

```text
Main-Class: app.MiniBank
```
