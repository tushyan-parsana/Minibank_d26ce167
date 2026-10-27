package edu.charusat.minibank.model;
import edu.charusat.minibank.exception.InvalidAmountException;
import edu.charusat.minibank.exception.InsufficientFundsException;
public interface Transactable {
    void deposit(long amount) throws InvalidAmountException;
    boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException;
}
