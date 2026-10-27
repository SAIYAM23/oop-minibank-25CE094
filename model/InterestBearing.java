package model;

public interface InterestBearing {
    default double yearlyInterest() {
        if (this instanceof Account account) {
            return account.getBalance() * account.interestRate() / 100.0;
        }

        return 0.0;
    }
}
