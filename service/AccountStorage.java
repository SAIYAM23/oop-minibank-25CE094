package service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.Account;

public class AccountStorage {

    private static final String FILE_NAME = "accounts.dat";

    private static class AccountData implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private final Account[] accounts;
        private final String[] ownerNames;

        AccountData(Account[] accounts) {
            this.accounts = accounts;
            this.ownerNames = new String[accounts.length];

            for (int i = 0; i < accounts.length; i++) {
                ownerNames[i] = accounts[i].getOwnerName();
            }
        }
    }

    public static void save(Account[] accounts) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream(FILE_NAME))) {
            output.writeObject(new AccountData(accounts));
        }
    }

    public static Account[] load()
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(FILE_NAME))) {

            AccountData data = (AccountData) input.readObject();

            for (int i = 0; i < data.accounts.length; i++) {
                data.accounts[i].restoreOwnerName(data.ownerNames[i]);
            }

            return data.accounts;
        }
    }
}