package service;

import model.Account;
import exception.BankException;

public class DeadlockDemo {

    public void demonstrateSafeLocking(Account accountA, Account accountB) {

        Thread first = new Thread(
                () -> transferSafely(accountA, accountB, 100),
                "Transfer-A-B");

        Thread second = new Thread(
                () -> transferSafely(accountB, accountA, 100),
                "Transfer-B-A");

        first.start();
        second.start();

        try {
            first.join();
            second.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Deadlock demo interrupted.");
        }
    }

    private void transferSafely(
            Account source, Account destination, long amount) {

        // Always acquire the account locks in account-number order.
        Account firstLock;
        Account secondLock;

        if (source.getAccountNumber().compareTo(
                destination.getAccountNumber()) < 0) {
            firstLock = source;
            secondLock = destination;
        } else {
            firstLock = destination;
            secondLock = source;
        }

        try {
            synchronized (firstLock) {
                synchronized (secondLock) {
                    source.transfer(destination, amount);

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " completed safely.");
                }
            }
        } catch (BankException e) {
            System.out.println(
                    Thread.currentThread().getName()
                            + " failed: " + e.getMessage());
        }
    }
}