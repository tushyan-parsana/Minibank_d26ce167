package edu.charusat.minibank;
import edu.charusat.minibank.exception.*;
import edu.charusat.minibank.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {
    @Test void depositIncreasesBalance() throws Exception {
        Account account = new CurrentAccount("Test User", 100, 500);
        account.deposit(50);
        assertEquals(150, account.getBalance());
    }
    @Test void withdrawalOverLimitThrows() {
        Account account = new SavingsAccount("Test User", 1000, 500);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(600));
    }
    @Test void concurrentDepositsRemainCorrect() throws Exception {
        Account account = new CurrentAccount("Concurrent User", 0, 1_000_000);
        Thread[] threads = new Thread[10];
        for (int i=0; i<threads.length; i++) {
            threads[i] = new Thread(() -> { for (int j=0; j<1000; j++) {
                try { account.deposit(1); } catch (InvalidAmountException e) { throw new RuntimeException(e); }
            }});
            threads[i].start();
        }
        for (Thread thread : threads) thread.join();
        assertEquals(10_000, account.getBalance());
    }
}
