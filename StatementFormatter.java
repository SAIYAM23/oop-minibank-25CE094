public class StatementFormatter {

    public static String buildStatement(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }

        StringBuilder statement = new StringBuilder();

        statement.append("========== ACCOUNT STATEMENT ==========\n");
        statement.append("Account Number: ")
                .append(account.getAccountNumber()).append('\n');
        statement.append("Account Holder: ")
                .append(account.getOwnerName()).append('\n');
        statement.append("Balance: ₹")
                .append(account.getBalance()).append('\n');
        statement.append("Status: ")
                .append(account.isActive() ? "Active" : "Inactive").append('\n');
        statement.append("=======================================");

        return statement.toString();
    }
}