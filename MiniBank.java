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

        System.out.println("\n--- Practical 3: Object Methods ---");

        // Print an account using toString()
        System.out.println("Account details: " + accounts[0]);

        // Compare two accounts
        Account anotherAccount = new Account("Riya", 4000);
        System.out.println(
            "Two separate accounts equal: "
            + accounts[0].equals(anotherAccount)
        );

        // Check object type using instanceof
        Object testObject = accounts[0];
        System.out.println(
            "Is testObject an Account? "
            + (testObject instanceof Account)
        );

        // Create a customer with an address
        Customer.Address address = new Customer.Address(
            "123 Main Road",
            "Anand",
            "388001"
        );

        Customer customer = new Customer(
            "Riya",
            "riya@example.com",
            "9876543210",
            address
        );

        Customer clonedCustomer = customer.clone();

        System.out.println("Customer ID: " + customer.getCustomerId());
        System.out.println("Customer name: " + customer.getName());
        System.out.println(
            "Customer city: " + customer.getAddress().getCity()
        );
        System.out.println(
            "Clone has same name: "
            + clonedCustomer.getName().equals(customer.getName())
        );
        System.out.println(
            "Clone is a different object: " + (customer != clonedCustomer)
        );

        System.out.println("\n--- Practical 4: Validation and Parsing ---");

        // 1. Test mobile validation
        System.out.println(
            "Valid mobile: " + Validator.isValidMobile("9876543210")
        );
        System.out.println(
            "Invalid mobile: " + Validator.isValidMobile("12345")
        );

        // 2. Test email validation
        System.out.println(
            "Valid email: " + Validator.isValidEmail("riya@example.com")
        );
        System.out.println(
            "Invalid email: " + Validator.isValidEmail("wrong-email")
        );

        // 3. Test PAN validation
        System.out.println(
            "Valid PAN: " + Validator.isValidPan("ABCDE1234F")
        );
        System.out.println(
            "Invalid PAN: " + Validator.isValidPan("12345")
        );

        // 4. Test IFSC validation
        System.out.println(
            "Valid IFSC: " + Validator.isValidIfsc("SBIN0001234")
        );
        System.out.println(
            "Invalid IFSC: " + Validator.isValidIfsc("BAD")
        );

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
            StatementFormatter.buildStatement(accounts[0])
        );

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