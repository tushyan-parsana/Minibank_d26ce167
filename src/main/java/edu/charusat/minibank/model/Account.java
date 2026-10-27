package edu.charusat.minibank.model;

import edu.charusat.minibank.exception.*;
import edu.charusat.minibank.model.annotation.Id;
import edu.charusat.minibank.model.annotation.Positive;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Account implements Transactable, InterestBearing, Serializable, Comparable<Account> {
    private static final long serialVersionUID = 1L;
    private static final AtomicLong ACCOUNT_COUNTER = new AtomicLong(0);
    @Id private final String accountNumber;
    private String ownerName;
    @Positive(message = "balance must be non-negative") private long balance;
    private boolean active = true;

    protected Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", ACCOUNT_COUNTER.incrementAndGet());
        this.ownerName = Objects.requireNonNull(ownerName, "ownerName");
        if (openingBalance < 0) throw new IllegalArgumentException("Opening balance cannot be negative");
        this.balance = openingBalance;
    }
    protected Account(String ownerName) { this(ownerName, 0); }
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public synchronized long getBalance() { return balance; }
    public boolean isActive() { return active; }
    public void closeAccount() { active = false; }
    protected synchronized void setBalanceInternal(long value) { balance = value; }

    @Override public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Deposit amount must be greater than zero.");
        if (!active) throw new InvalidAmountException("Account is inactive.");
        balance = Math.addExact(balance, amount);
    }
    @Override public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        if (!active) throw new InvalidAmountException("Account is inactive.");
        if (!canWithdraw(amount)) {
            long shortfall = Math.max(0, amount - balance);
            throw new InsufficientFundsException(shortfall);
        }
        balance -= amount;
        return true;
    }
    public abstract boolean canWithdraw(long amount);
    @Override public int compareTo(Account other) { return accountNumber.compareTo(other.accountNumber); }
    @Override public String toString() { return accountNumber + " | " + ownerName + " | Balance: ₹" + getBalance() + " | " + (active ? "ACTIVE" : "INACTIVE"); }
    @Override public boolean equals(Object o) { return this == o || (o instanceof Account account && accountNumber.equals(account.accountNumber)); }
    @Override public int hashCode() { return accountNumber.hashCode(); }

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) throw new BankException("Destination account is required.");
        if (to == this) throw new BankException("Source and destination must differ.");
        Account first = this.accountNumber.compareTo(to.accountNumber) < 0 ? this : to;
        Account second = first == this ? to : this;
        try {
            synchronized (first) {
                synchronized (second) {
                    this.withdraw(amount);
                    try { to.deposit(amount); }
                    catch (BankException | ArithmeticException e) {
                        // Roll back the debit if crediting the destination fails.
                        this.setBalanceInternal(this.getBalance() + amount);
                        if (e instanceof BankException be) throw be;
                        throw new BankException("Transfer failed due to balance overflow.");
                    }
                }
            }
        } catch (BankException e) { throw e; }
        finally { /* lock release is automatic */ }
    }
}
