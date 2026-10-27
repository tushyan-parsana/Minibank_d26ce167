package edu.charusat.minibank.util;
import edu.charusat.minibank.model.*;
public final class CommandParser {
    private CommandParser() {}
    public static Command parse(String line) {
        if (line == null) throw new IllegalArgumentException("Command cannot be null.");
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) throw new IllegalArgumentException("Use: DEPOSIT|WITHDRAW|TRANSFER ACCOUNT_NUMBER AMOUNT");
        try {
            TransactionType type = TransactionType.valueOf(parts[0].toUpperCase());
            long amount = Long.parseLong(parts[2]);
            return new Command(type, parts[1].toUpperCase(), amount);
        } catch (RuntimeException e) { throw new IllegalArgumentException("Invalid command or amount.", e); }
    }
}
