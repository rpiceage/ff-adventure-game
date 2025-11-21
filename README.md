# Adventure Game

A Java desktop application that runs text-based adventure games from YAML files.

## Requirements

- Java 11 or higher
- Maven

## Running the Application

Run with the default sample adventure:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.adventure.Main"
```

Run with a custom YAML file:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.adventure.Main" -Dexec.args="path/to/adventure.yaml"
```

## Running Tests

Run all tests:
```bash
mvn test
```
