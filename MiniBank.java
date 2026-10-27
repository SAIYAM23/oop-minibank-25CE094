import java.util.Scanner;

record BankInfo(String name, String branch) {}

enum MenuOption {
    OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
}

public class MiniBank {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        BankInfo bank = new BankInfo(
            "MiniBank",
            "CHARUSAT Main Branch"
        );

        Account[] accounts = {
            new Account("Riya", 5000),
            new Account("Aarav", 3000),
            new Account("Priya")
        };

        System.out.println("================================");
        System.out.println("       " + bank.name());
        System.out.println("       " + bank.branch());
        System.out.println("================================");

        System.out.println("\n--- Practical 2: Account Demo ---");

        accounts[0].deposit(2000);
        System.out.println(
            accounts[0].getOwnerName()
            + " balance after deposit: "
            + accounts[0].getBalance()
        );

        boolean success = accounts[0].withdraw(3000);
        System.out.println("Withdraw 3000 successful: " + success);
        System.out.println(
            "Balance: " + accounts[0].getBalance()
        );

        success = accounts[0].withdraw(10000);
        System.out.println("Withdraw 10000 successful: " + success);
        System.out.println(
            "Balance: " + accounts[0].getBalance()
        );

        System.out.println("\nAll Accounts:");
        for (Account account : accounts) {
            System.out.println(
                account.getAccountNumber()
                + " | "
                + account.getOwnerName()
                + " | "
                + account.getBalance()
            );
        }

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