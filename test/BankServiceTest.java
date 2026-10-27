
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

import exception.BankException;
import exception.InsufficientFundsException;
import model.Account;
import model.SavingsAccount;
import service.BankService;

class BankServiceTest {

    @Test
    void depositIncreasesBalance() throws Exception {
        Account account = new SavingsAccount("Test User", 5000, 1000);
        long initialBalance = account.getBalance();

        account.deposit(1000);

        assertEquals(initialBalance + 1000, account.getBalance());
    }

    @Test
    void withdrawalBeyondLimitThrowsException() {
        Account account = new SavingsAccount("Test User", 5000, 1000);

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(4500)
        );
    }

    @Test
    void concurrentDepositsPreserveCorrectBalance() throws Exception {
        Account account = new SavingsAccount("Concurrent User", 5000, 1000);

        int numberOfDeposits = 100;
        long depositAmount = 10;

        CountDownLatch latch = new CountDownLatch(numberOfDeposits);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfDeposits; i++) {
            Thread thread = new Thread(() -> {
                try {
                    account.deposit(depositAmount);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });

            threads.add(thread);
            thread.start();
        }

        latch.await();

        assertEquals(
                5000 + numberOfDeposits * depositAmount,
                account.getBalance()
        );
    }
}
