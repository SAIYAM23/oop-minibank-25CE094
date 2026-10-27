package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TransactionLogger {

    private static final Path LOG_FILE = Path.of("data", "transactions.log");

    public static void logTransaction(
            String type, String accountNumber, long amount)
            throws IOException {

        if (!type.equals("DEPOSIT") && !type.equals("WITHDRAW")) {
            throw new IllegalArgumentException(
                    "Transaction type must be DEPOSIT or WITHDRAW.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be positive.");
        }

        Files.createDirectories(LOG_FILE.getParent());

        String entry = type + " " + accountNumber + " " + amount
                + System.lineSeparator();

        Files.writeString(
                LOG_FILE,
                entry,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }
}
