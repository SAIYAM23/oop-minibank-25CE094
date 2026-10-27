package service;

import service.TransactionProcessor;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import util.StatementFormatter;
import util.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class BankService {

    private final Repository<Account> repository = new Repository<>();

    public Account openAccount(
            String ownerName,
            String accountType,
            long openingBalance) throws BankException {

        if (ownerName == null || ownerName.isBlank()) {
            throw new BankException("Owner name cannot be empty.");
        }

        if (openingBalance < 0) {
            throw new InvalidAmountException(
                    "Opening balance cannot be negative.");
        }

        Account account;

        if ("SAVINGS".equalsIgnoreCase(accountType)) {
            account = new SavingsAccount(ownerName, openingBalance, 1000);
        } else if ("CURRENT".equalsIgnoreCase(accountType)) {
            account = new CurrentAccount(ownerName, openingBalance, 5000);
        } else {
            throw new BankException(
                    "Choose SAVINGS or CURRENT.");
        }

        repository.save(account);
        return account;
    }

    public void deposit(String accountNumber, long amount)
            throws BankException {
        Account account = requireAccount(accountNumber);
        account.deposit(amount);
        logTransaction("DEPOSIT", accountNumber, amount);
    }

    public void withdraw(String accountNumber, long amount)
            throws BankException {
        Account account = requireAccount(accountNumber);
        account.withdraw(amount);
        logTransaction("WITHDRAW", accountNumber, amount);
    }

    public void transfer(
            String fromAccountNumber,
            String toAccountNumber,
            long amount) throws BankException {

        Account from = requireAccount(fromAccountNumber);
        Account to = requireAccount(toAccountNumber);

        if (from == to) {
            throw new BankException(
                    "Source and destination accounts must differ.");
        }

        // Validate before modifying either account.
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be greater than zero.");
        }

        if (!from.canWithdraw(amount)) {
            throw new exception.InsufficientFundsException(
                    "Transfer denied. Insufficient available funds.",
                    Math.max(0, amount - from.getBalance()));
        }

        from.transfer(to, amount);

        logTransaction("WITHDRAW", fromAccountNumber, amount);
        logTransaction("DEPOSIT", toAccountNumber, amount);
    }

    public String statement(String accountNumber)
            throws AccountNotFoundException {
        return StatementFormatter.buildStatement(
                requireAccount(accountNumber));
    }

    public ArrayList<Account> listAccounts() {
        return repository.findAll();
    }

    public void loadAccounts() throws IOException, ClassNotFoundException {
        try {
            Account[] savedAccounts = AccountStorage.load();

            for (Account account : savedAccounts) {
                repository.save(account);
                Account.restoreAccountCounter(account.getAccountNumber());
            }

            System.out.println("Loaded " + savedAccounts.length + " saved account(s).");

        } catch (java.io.FileNotFoundException e) {
            System.out.println("No saved accounts found. Starting fresh.");
        }
    }

    public void saveAccounts() throws IOException {
        AccountStorage.save(
                repository.findAll().toArray(new Account[0]));
    }

    private Account requireAccount(String accountNumber)
            throws AccountNotFoundException {
        Account account = repository.findById(accountNumber);

        if (account == null || !account.isActive()) {
            throw new AccountNotFoundException(
                    "Account not found or inactive: " + accountNumber);
        }

        return account;
    }

    private void logTransaction(
            String type, String accountNumber, long amount) {
        try {
            TransactionLogger.logTransaction(
                    type, accountNumber, amount);
        } catch (IOException e) {
            System.err.println(
                    "Warning: transaction could not be logged: "
                            + e.getMessage());
        }
    }

    public boolean isValidMobile(String mobile) {
        return Validator.isValidMobile(mobile);
    }

    public boolean isValidEmail(String email) {
        return Validator.isValidEmail(email);
    }

    public void processConcurrentDeposits(
            String accountNumber, long amount, int transactionCount)
            throws BankException {

        if (transactionCount <= 0) {
            throw new BankException(
                    "Transaction count must be greater than zero.");
        }

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero.");
        }

        Account account = requireAccount(accountNumber);

        TransactionProcessor processor = new TransactionProcessor();
        processor.addAccount(account);

        CountDownLatch latch = new CountDownLatch(transactionCount);
        AtomicReference<Exception> failure = new AtomicReference<>();

        try {
            for (int i = 0; i < transactionCount; i++) {
                processor.submit(() -> {
                    try {
                        Account sharedAccount = processor.getAccount(accountNumber);

                        if (sharedAccount == null) {
                            throw new IllegalStateException(
                                    "Account missing from transaction store.");
                        }

                        sharedAccount.deposit(amount);
                    } catch (Exception e) {
                        failure.compareAndSet(null, e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BankException(
                        "Batch processing was interrupted.");
            }
        } finally {
            processor.stop();
        }

        if (failure.get() != null) {
            throw new BankException(
                    "Batch processing failed: "
                            + failure.get().getMessage());
        }

        for (int i = 0; i < transactionCount; i++) {
            logTransaction("DEPOSIT", accountNumber, amount);
        }

        System.out.println("Completed " + transactionCount
                + " concurrent deposits of ₹" + amount + ".");
        System.out.println("Final balance: ₹" + account.getBalance());
    }

}