package service;

import model.Command;
import model.TransactionType;

public class CommandParser {

    public static Command parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Command cannot be empty.");
        }

        String[] parts = line.trim().split("\\s+");

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Command must contain exactly 3 parts: TYPE ACCOUNT_NUMBER AMOUNT"
            );
        }

        TransactionType type;

        try {
            type = TransactionType.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid transaction type. Use DEPOSIT, WITHDRAW or TRANSFER."
            );
        }

        String accountNumber = parts[1];

        if (!accountNumber.matches("AC[0-9]{4}")) {
            throw new IllegalArgumentException(
                    "Invalid account number. Expected format: AC0001"
            );
        }

        long amount;

        try {
            amount = Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a whole number.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        return new Command(type, accountNumber, amount);
    }
}