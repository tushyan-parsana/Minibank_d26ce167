package edu.charusat.minibank.exception;
public class InsufficientFundsException extends BankException {
    private final long shortfall;
    public InsufficientFundsException(long shortfall) {
        super("Insufficient funds. Shortfall: ₹" + shortfall);
        this.shortfall = shortfall;
    }
    public long getShortfall() { return shortfall; }
}
