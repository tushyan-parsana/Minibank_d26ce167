package edu.charusat.minibank.repository;
import edu.charusat.minibank.model.Account;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class AccountStore {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final List<String> history = Collections.synchronizedList(new ArrayList<>());
    public void add(Account account) { accounts.put(account.getAccountNumber(), account); }
    public Account get(String accountNumber) { return accounts.get(accountNumber); }
    public ArrayList<Account> getAll() { return new ArrayList<>(accounts.values()); }
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> result = getAll();
        result.sort(Comparator.comparingLong(Account::getBalance));
        return result;
    }
    public void addHistory(String entry) { history.add(entry); }
    public ArrayList<String> getHistory() { synchronized(history) { return new ArrayList<>(history); } }
}
