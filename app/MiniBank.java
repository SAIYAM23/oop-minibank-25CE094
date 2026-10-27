package app;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;

import static util.Validator.isValidMobile;
import util.AnnotationValidator;
import util.Validator;
import util.StatementFormatter;
import util.BankSession;

import model.Account;
import model.Customer;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.Command;
import model.TransactionType;
import model.WithdrawRule;
import model.AccountWorker;

import service.CommandParser;
import service.TransactionProcessor;
import service.TransactionBuffer;
import service.DeadlockDemo;
import service.AccountStorage;
import service.TransactionLogger;
import service.EndOfDayReport;

import java.lang.reflect.Field;
import java.io.IOException;
import java.util.Scanner;

record BankInfo(String name, String branch) {
}

enum MenuOption {
    OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
}

public class MiniBank {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        BankInfo bank = new BankInfo(
                "MiniBank",
                "CHARUSAT Main Branch");

        Account[] accounts = {
                new SavingsAccount("Riya", 5000, 1000),
                new CurrentAccount("Aarav", 3000, 1000),
                new FixedDepositAccount("Priya", 0)
        };

        System.out.println("================================");
        System.out.println("       " + bank.name());
        System.out.println("       " + bank.branch());
        System.out.println("================================");

        System.out.println("\n--- Practical 2: Account Demo ---");

        try {
            accounts[0].deposit(2000);
            System.out.println(
                    accounts[0].getOwnerName()
                            + " balance after deposit: "
                            + accounts[0].getBalance());
        } catch (InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        } finally {
            System.out.println("Deposit attempt completed.");
        }

        try {
            boolean success = accounts[0].withdraw(3000);
            System.out.println("Withdraw 3000 successful: " + success);
            System.out.println("Balance: " + accounts[0].getBalance());
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
            System.out.println("Shortfall: ₹" + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        } finally {
            System.out.println("Withdrawal attempt completed.");
        }

        try {
            boolean success = accounts[0].withdraw(10000);
            System.out.println("Withdraw 10000 successful: " + success);
            System.out.println("Balance: " + accounts[0].getBalance());
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
            System.out.println("Shortfall: ₹" + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        } finally {
            System.out.println("Withdrawal attempt completed.");
        }

        System.out.println("\nAll Accounts:");
        for (Account account : accounts) {
            System.out.println(
                    account.getAccountNumber()
                            + " | "
                            + account.getOwnerName()
                            + " | "
                            + account.getBalance());
        }

        System.out.println("\n--- Practical 3: Object Methods ---");

        // Print an account using toString()
        System.out.println("Account details: " + accounts[0]);

        // Compare two accounts
        Account anotherAccount = new SavingsAccount("Riya", 4000, 1000);
        System.out.println(
                "Two separate accounts equal: "
                        + accounts[0].equals(anotherAccount));

        // Check object type using instanceof
        Object testObject = accounts[0];
        System.out.println(
                "Is testObject an Account? "
                        + (testObject instanceof Account));

        // Create a customer with an address
        Customer.Address address = new Customer.Address(
                "123 Main Road",
                "Anand",
                "388001");

        Customer customer = new Customer(
                "Riya",
                "riya@example.com",
                "9876543210",
                address);

        Customer clonedCustomer = customer.clone();

        System.out.println("Customer ID: " + customer.getCustomerId());
        System.out.println("Customer name: " + customer.getName());
        System.out.println(
                "Customer city: " + customer.getAddress().getCity());
        System.out.println(
                "Clone has same name: "
                        + clonedCustomer.getName().equals(customer.getName()));
        System.out.println(
                "Clone is a different object: " + (customer != clonedCustomer));

        System.out.println("\n--- Practical 4: Validation and Parsing ---");

        // 1. Test mobile validation
        System.out.println(
                "Valid mobile: " + isValidMobile("9876543210"));
        System.out.println(
                "Invalid mobile: " + isValidMobile("12345"));

        // 2. Test email validation
        System.out.println(
                "Valid email: " + Validator.isValidEmail("riya@example.com"));
        System.out.println(
                "Invalid email: " + Validator.isValidEmail("wrong-email"));

        // 3. Test PAN validation
        System.out.println(
                "Valid PAN: " + Validator.isValidPan("ABCDE1234F"));
        System.out.println(
                "Invalid PAN: " + Validator.isValidPan("12345"));

        // 4. Test IFSC validation
        System.out.println(
                "Valid IFSC: " + Validator.isValidIfsc("SBIN0001234"));
        System.out.println(
                "Invalid IFSC: " + Validator.isValidIfsc("BAD"));

        // 5. Parse a banking command
        try {
            Command command = CommandParser.parse("DEPOSIT AC0001 500");

            System.out.println("Parsed command type: " + command.type());
            System.out.println("Parsed account: " + command.accountNumber());
            System.out.println("Parsed amount: " + command.amount());
        } catch (IllegalArgumentException e) {
            System.out.println("Command error: " + e.getMessage());
        }

        // 6. Display an account statement
        System.out.println(
                StatementFormatter.buildStatement(accounts[0]));

        System.out.println("\n--- Practical 5: Inheritance and Polymorphism ---");

        for (Account account : accounts) {
            System.out.println(
                    account.getOwnerName()
                            + " | Account: " + account.getAccountNumber()
                            + " | Interest rate: " + account.interestRate() + "%");

            if (account instanceof SavingsAccount savings) {
                System.out.println(
                        "Minimum balance: " + savings.getMinBalance());
            }
        }

        System.out.println(
                "Savings can withdraw 1000: " + accounts[0].canWithdraw(1000));

        System.out.println(
                "Current can withdraw 3500: " + accounts[1].canWithdraw(3500));

        System.out.println(
                "Fixed deposit can withdraw 100: " + accounts[2].canWithdraw(100));

        System.out.println("\n--- Practical 6: Interfaces and Lambdas ---");

        // 1. Test the default method from InterestBearing
        for (Account account : accounts) {
            System.out.println(
                    account.getOwnerName()
                            + " yearly interest: ₹"
                            + String.format("%.2f", account.yearlyInterest()));
        }

        // 2. WithdrawRule using an anonymous class
        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };

        // 3. WithdrawRule using a lambda expression
        WithdrawRule lambdaRule = (account, amount) -> account.canWithdraw(amount);

        System.out.println(
                "Anonymous class allows savings withdrawal of 1000: "
                        + anonymousRule.allow(accounts[0], 1000));

        System.out.println(
                "Lambda allows savings withdrawal of 1000: "
                        + lambdaRule.allow(accounts[0], 1000));

        // 4. Test the Premium marker interface
        System.out.println(
                "Is savings account Premium? "
                        + (accounts[0] instanceof model.Premium));

        System.out.println("\n--- Practical 7: Annotations and Reflection ---");

        Account invalidAccount = new SavingsAccount("Test User", 1000, 0);

        try {
            Field balanceField = Account.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.setLong(invalidAccount, -100);
        } catch (ReflectiveOperationException e) {
            System.out.println("Unable to prepare invalid account: " + e.getMessage());
        }

        String[] invalidErrors = AnnotationValidator.validate(invalidAccount);

        System.out.println("Invalid account validation:");
        for (String error : invalidErrors) {
            System.out.println(error);
        }

        String[] validErrors = AnnotationValidator.validate(accounts[0]);
        System.out.println("Valid account error count: " + validErrors.length);

        System.out.println("\n--- Practical 8: Exception Handling ---");

        // Test an insufficient-funds exception and its shortfall.
        try {
            accounts[0].withdraw(10000);
        } catch (InsufficientFundsException e) {
            System.out.println("Friendly error: " + e.getMessage());
            System.out.println("Shortfall: ₹" + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Invalid withdrawal: " + e.getMessage());
        } finally {
            System.out.println("Withdrawal test completed.");
        }

        // Test a successful transfer.
        try {
            accounts[1].transfer(accounts[0], 500);
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // Test try-with-resources even when an exception occurs.
        try (BankSession session = new BankSession()) {
            session.performOperation();
            throw new IllegalStateException(
                    "Simulated banking operation failure.");
        } catch (IllegalStateException e) {
            System.out.println("Session error: " + e.getMessage());
        }

        // Test invalid deposit amount.
        try {
            accounts[0].deposit(0);
        } catch (InvalidAmountException e) {
            System.out.println("Invalid deposit caught: " + e.getMessage());
        }

        // Test invalid withdrawal amount.
        try {
            accounts[0].withdraw(-100);
        } catch (InvalidAmountException e) {
            System.out.println(
                    "Invalid withdrawal caught: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println(
                    "Insufficient funds: " + e.getMessage());
        }

        // Test failed transfer to a null destination.
        try {
            accounts[0].transfer(null, 100);
        } catch (BankException e) {
            System.out.println("Invalid transfer caught: " + e.getMessage());
        }

        // Practical 9: Multithreading and Race Condition
        System.out.println("\n===== Practical 9: Race Condition =====");

        Account raceAccount = new SavingsAccount("Thread Test", 0, 0);

        int numberOfThreads = 10;
        int depositsPerThread = 100_000;
        long depositAmount = 1;

        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            AccountWorker worker = new AccountWorker(
                    raceAccount, depositsPerThread, depositAmount);

            threads[i] = new Thread(worker, "Worker-" + (i + 1));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Main thread was interrupted.");
                return;
            }
        }

        long expectedBalance = (long) numberOfThreads * depositsPerThread * depositAmount;

        System.out.println("Expected balance: ₹" + expectedBalance);
        System.out.println("Actual balance:   ₹" + raceAccount.getBalance());

        // Practical 10: Transaction Processor
        System.out.println("\n===== Practical 10: Transaction Processor =====");

        Account processorAccount = new SavingsAccount("Processor Test", 0, 0);

        TransactionProcessor processor = new TransactionProcessor();

        for (int i = 1; i <= 10; i++) {
            final int taskId = i;

            processor.submit(() -> {
                try {
                    processorAccount.deposit(100);

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " processed transaction " + taskId);
                } catch (InvalidAmountException e) {
                    System.out.println("Transaction failed: " + e.getMessage());
                }
            });
        }

        processor.stop();

        System.out.println("Expected balance: ₹1000");
        System.out.println(
                "Actual balance:   ₹" + processorAccount.getBalance());

        // Practical 10: Producer-Consumer
        System.out.println("\n===== Practical 10: Producer-Consumer =====");

        TransactionBuffer buffer = new TransactionBuffer(3);
        int numberOfItems = 10;

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= numberOfItems; i++) {
                    buffer.produce(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Producer interrupted.");
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= numberOfItems; i++) {
                    buffer.consume();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Consumer interrupted.");
            }
        }, "Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread interrupted.");
            return;
        }

        System.out.println("Producer-consumer test completed.");

        // Practical 10: Deadlock Prevention
        System.out.println("\n===== Practical 10: Deadlock Prevention =====");

        Account deadlockAccountA = new SavingsAccount("Deadlock A", 1000, 0);

        Account deadlockAccountB = new SavingsAccount("Deadlock B", 1000, 0);

        DeadlockDemo deadlockDemo = new DeadlockDemo();

        deadlockDemo.demonstrateSafeLocking(
                deadlockAccountA, deadlockAccountB);

        System.out.println("Deadlock prevention test completed.");

        // Practical 11: Persistence, Transaction Logs, and Reports
        System.out.println("\n===== Practical 11: Persistence and Reports =====");

        try {
            // Save the three original accounts.
            AccountStorage.save(accounts);
            System.out.println("Accounts saved to accounts.dat.");

            // Load accounts back from the file.
            Account[] loadedAccounts = AccountStorage.load();
            System.out.println("\nReloaded accounts:");

            for (Account account : loadedAccounts) {
                System.out.println(
                        account.getAccountNumber()
                                + " | " + account.getOwnerName()
                                + " | Balance: ₹" + account.getBalance());
            }

            // Append example transactions to the log.
            TransactionLogger.logTransaction(
                    "DEPOSIT", accounts[0].getAccountNumber(), 500);

            TransactionLogger.logTransaction(
                    "WITHDRAW", accounts[1].getAccountNumber(), 200);

            System.out.println("\nTransactions appended to the log.");

            // Generate the end-of-day report.
            EndOfDayReport.generateReport();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Practical 11 error: " + e.getMessage());
        }

        // Practical 12: Collections and Comparable/Comparator
        System.out.println("\n===== Practical 12: Collections =====");

        service.AccountStore accountStore = new service.AccountStore();

        Account storeAccount1 = new SavingsAccount("Store Riya", 5000, 1000);

        Account storeAccount2 = new CurrentAccount("Store Aarav", 2000, 1000);

        Account storeAccount3 = new SavingsAccount("Store Priya", 9000, 1000);

        // Add accounts to the HashMap-based store.
        accountStore.add(storeAccount1);
        accountStore.add(storeAccount2);
        accountStore.add(storeAccount3);

        // Look up an account by account number.
        System.out.println("\nAccount lookup:");
        System.out.println(
                accountStore.get(storeAccount2.getAccountNumber()));

        // Display accounts sorted by balance, highest first.
        System.out.println("\nAccounts sorted by balance:");
        for (Account account : accountStore.sortedByBalance()) {
            System.out.println(
                    account.getAccountNumber()
                            + " | " + account.getOwnerName()
                            + " | Balance: ₹" + account.getBalance());
        }

        // Demonstrate Comparable<Account> using account numbers.
        System.out.println("\nAccounts sorted by account number:");
        java.util.ArrayList<Account> accountsByNumber = accountStore.getAll();

        java.util.Collections.sort(accountsByNumber);

        for (Account account : accountsByNumber) {
            System.out.println(
                    account.getAccountNumber()
                            + " | " + account.getOwnerName());
        }

        // Demonstrate ConcurrentHashMap in TransactionProcessor.
        TransactionProcessor collectionProcessor = new TransactionProcessor();

        collectionProcessor.addAccount(storeAccount1);
        collectionProcessor.addAccount(storeAccount2);
        collectionProcessor.addAccount(storeAccount3);

        System.out.println(
                "\nAccounts in ConcurrentHashMap: "
                        + collectionProcessor.getSharedAccounts().size());

        collectionProcessor.stop();

        boolean running = true;

        while (running) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String input = sc.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number from 1 to 5.");
                continue;
            }

            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.EXIT;
                default -> null;
            };

            if (option == null) {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }

            String message = switch (option) {
                case OPEN_ACCOUNT -> "Open Account - to be implemented in a later lab.";
                case DEPOSIT -> "Deposit - to be implemented in a later lab.";
                case WITHDRAW -> "Withdraw - to be implemented in a later lab.";
                case TRANSFER -> "Transfer - to be implemented in a later lab.";
                case EXIT -> "Thank you for using MiniBank!";
            };

            System.out.println(message);

            if (option == MenuOption.EXIT) {
                running = false;
            }
        }

        sc.close();
    }
}