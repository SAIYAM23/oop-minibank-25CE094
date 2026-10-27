package model;

import java.util.Objects;
import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import exception.BankException;
import exception.AccountNotFoundException;

public abstract class Account implements Transactable, InterestBearing,
        java.io.Serializable, Comparable<Account> {

    private static final long serialVersionUID = 1L;
    @Id
    @MaxLength(10)
    private final String accountNumber;
    private transient String ownerName;
    @Positive
    private long balance;
    private boolean active;

    private static long accountCounter = 0;

    private static String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance >= 0 ? openingBalance : 0;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero.");
        }

        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be greater than zero.");
        }

        if (!canWithdraw(amount)) {
            long shortfall = Math.max(0, amount - balance);

            throw new InsufficientFundsException(
                    "Withdrawal denied. Insufficient available funds.",
                    shortfall);
        }

        balance -= amount;
        return true;
    }

    public void transfer(Account to, long amount) throws BankException {
        try {
            if (to == null || !to.isActive()) {
                throw new AccountNotFoundException(
                        "Destination account was not found or is inactive.");
            }

            if (to == this) {
                throw new BankException(
                        "Cannot transfer to the same account.");
            }

            if (amount <= 0) {
                throw new InvalidAmountException(
                        "Transfer amount must be greater than zero.");
            }

            // Check the source before changing either balance.
            if (!canWithdraw(amount)) {
                long shortfall = Math.max(0, amount - getBalance());
                throw new InsufficientFundsException(
                        "Transfer denied. Insufficient available funds.",
                        shortfall);
            }

            // Validate all known failure conditions before changing balances.
            withdraw(amount);
            to.deposit(amount);

            System.out.println(
                    "Transfer of ₹" + amount + " to "
                            + to.getOwnerName() + " successful.");

        } catch (BankException e) {
            throw e;
        } finally {
            System.out.println("Transfer attempt completed.");
        }
    }

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void restoreOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | " + balance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account other)) {
            return false;
        }
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }
}
