import java.util.Scanner;

// Read-only bank information
record BankInfo(String name, String branch) {}

// Fixed set of actions available in MiniBank
enum MenuOption {
    OPEN_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    EXIT
}

public class MiniBank {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Display application header
        BankInfo bank = new BankInfo(
                "MiniBank",
                "CHARUSAT Main Branch"
        );

        System.out.println("================================");
        System.out.println("       " + bank.name());
        System.out.println("       " + bank.branch());
        System.out.println("================================");

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

            // Handle non-numeric menu input
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input! Please enter a number from 1 to 5."
                );
                continue;
            }

            // Convert the number into a MenuOption
            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.EXIT;
                default -> null;
            };

            if (option == null) {
                System.out.println(
                        "Invalid choice! Please enter a number from 1 to 5."
                );
                continue;
            }

            // Display a placeholder for each selected option
            String message = switch (option) {
                case OPEN_ACCOUNT ->
                        "Open Account - to be implemented in a later lab.";
                case DEPOSIT ->
                        "Deposit - to be implemented in a later lab.";
                case WITHDRAW ->
                        "Withdraw - to be implemented in a later lab.";
                case TRANSFER ->
                        "Transfer - to be implemented in a later lab.";
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
