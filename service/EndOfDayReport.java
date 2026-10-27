package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class EndOfDayReport {

    private static final Path LOG_DIRECTORY = Paths.get("data");
    private static final Path REPORT_FILE =
            LOG_DIRECTORY.resolve("end-of-day-report.txt");

    public static void generateReport() throws IOException {
        Files.createDirectories(LOG_DIRECTORY);

        long totalDeposits = 0;
        long totalWithdrawals = 0;
        int transactionCount = 0;

        Path transactionLog = LOG_DIRECTORY.resolve("transactions.log");

        if (Files.exists(transactionLog)) {
            try (Stream<String> lines = Files.lines(transactionLog)) {
                for (String line : (Iterable<String>) lines::iterator) {
                    String[] parts = line.trim().split("\\s+");

                    if (parts.length != 3) {
                        continue;
                    }

                    try {
                        long amount = Long.parseLong(parts[2]);

                        if (amount <= 0) {
                            continue;
                        }

                        if (parts[0].equals("DEPOSIT")) {
                            totalDeposits += amount;
                            transactionCount++;
                        } else if (parts[0].equals("WITHDRAW")) {
                            totalWithdrawals += amount;
                            transactionCount++;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid log entry: " + line);
                    }
                }
            }
        }

        long netChange = totalDeposits - totalWithdrawals;

        StringBuilder report = new StringBuilder();
        report.append("MINIBANK END-OF-DAY REPORT\n");
        report.append("=========================\n");
        report.append("Transactions processed: ").append(transactionCount).append('\n');
        report.append("Total deposits: ₹").append(totalDeposits).append('\n');
        report.append("Total withdrawals: ₹").append(totalWithdrawals).append('\n');
        report.append("Net change: ₹").append(netChange).append('\n');

        report.append("\nLOG FILE DETAILS\n");
        report.append("================\n");

        if (Files.exists(LOG_DIRECTORY)) {
            try (Stream<Path> paths = Files.walk(LOG_DIRECTORY)) {
                for (Path path : (Iterable<Path>) paths::iterator) {
                    if (Files.isRegularFile(path)
                            && !path.equals(REPORT_FILE)) {
                        BasicFileAttributes attributes =
                                Files.readAttributes(
                                        path, BasicFileAttributes.class);

                        report.append(path.getFileName())
                                .append(" | Size: ")
                                .append(attributes.size())
                                .append(" bytes | Modified: ")
                                .append(attributes.lastModifiedTime())
                                .append('\n');
                    }
                }
            }
        }

        Files.writeString(REPORT_FILE, report.toString());
        System.out.println(report);
        System.out.println("Report saved to: " + REPORT_FILE);
    }
}
