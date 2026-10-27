package edu.charusat.minibank.service;
import edu.charusat.minibank.exception.*;
import edu.charusat.minibank.model.*;
import edu.charusat.minibank.repository.*;
import edu.charusat.minibank.util.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BankService {
    private final Repository<Account> repository = new Repository<>();
    private final Map<String, Account> concurrentAccounts = new ConcurrentHashMap<>();
    private final AccountStore store = new AccountStore();
    private final Path logFile = Path.of("logs", "transactions.log");

    public synchronized Account openAccount(String name, String type, long openingBalance) throws BankException {
        if (name == null || name.isBlank()) throw new BankException("Owner name is required.");
        if (openingBalance < 0) throw new InvalidAmountException("Opening balance cannot be negative.");
        Account account = switch (type.toUpperCase(Locale.ROOT)) {
            case "S", "SAVINGS" -> new SavingsAccount(name, openingBalance, 500);
            case "C", "CURRENT" -> new CurrentAccount(name, openingBalance, 5_000);
            case "F", "FD", "FIXED DEPOSIT" -> new FixedDepositAccount(name, openingBalance);
            default -> throw new BankException("Choose S (Savings), C (Current), or F (Fixed Deposit).");
        };
        repository.save(account); concurrentAccounts.put(account.getAccountNumber(), account); store.add(account);
        log("OPEN " + account.getAccountNumber() + " " + openingBalance);
        return account;
    }
    public void deposit(String accountNumber, long amount) throws BankException {
        Account a = requireAccount(accountNumber); a.deposit(amount); log("DEPOSIT " + a.getAccountNumber() + " " + amount);
    }
    public void withdraw(String accountNumber, long amount) throws BankException {
        Account a = requireAccount(accountNumber); a.withdraw(amount); log("WITHDRAW " + a.getAccountNumber() + " " + amount);
    }
    public void transfer(String from, String to, long amount) throws BankException {
        Account source = requireAccount(from), destination = requireAccount(to);
        source.transfer(destination, amount); log("TRANSFER " + from + " " + amount + " TO " + to);
    }
    public String statement(String accountNumber) throws BankException { return StatementFormatter.buildStatement(requireAccount(accountNumber)); }
    public ArrayList<Account> listAccounts() { return store.sortedByBalance(); }
    public Repository<Account> getRepository() { return repository; }
    public Map<String, Account> getConcurrentAccounts() { return concurrentAccounts; }
    public void loadAccounts(Account[] accounts) {
        for (Account a : accounts) { repository.save(a); concurrentAccounts.put(a.getAccountNumber(), a); store.add(a); }
    }
    public Account[] accountArray() { return store.getAll().toArray(new Account[0]); }
    public String report() throws IOException { return ReportGenerator.generate(Path.of("logs"), Path.of("reports", "daily-report.txt")); }
    private Account requireAccount(String number) throws AccountNotFoundException {
        Account a = repository.findById(number);
        if (a == null) throw new AccountNotFoundException(number);
        return a;
    }
    private void log(String entry) throws BankException {
        store.addHistory(entry);
        try { TransactionLog.append(logFile, entry); }
        catch (IOException e) { throw new BankException("Transaction succeeded, but log could not be written: " + e.getMessage()); }
    }
}
