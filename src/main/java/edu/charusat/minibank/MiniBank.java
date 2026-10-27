package edu.charusat.minibank;

import edu.charusat.minibank.exception.BankException;
import edu.charusat.minibank.model.*;
import edu.charusat.minibank.repository.Repository;
import edu.charusat.minibank.service.*;
import edu.charusat.minibank.util.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class MiniBank {
    private static final Path STATE_FILE = Path.of("data", "accounts.ser");
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final BankService BANK = new BankService();

    public static void main(String[] args) {
        System.out.println(new BankInfo("MiniBank", "CHARUSAT Student Branch"));
        try { BANK.loadAccounts(StatePersister.load(STATE_FILE)); }
        catch (IOException | ClassNotFoundException e) { System.out.println("Starting with empty account store: " + e.getMessage()); }

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose option: ");
            try {
                switch (choice) {
                    case 1 -> openAccount();
                    case 2 -> BANK.deposit(readText("Account number: "), readLong("Deposit amount (₹): "));
                    case 3 -> BANK.withdraw(readText("Account number: "), readLong("Withdrawal amount (₹): "));
                    case 4 -> BANK.transfer(readText("From account: "), readText("To account: "), readLong("Transfer amount (₹): "));
                    case 5 -> System.out.println(BANK.statement(readText("Account number: ")));
                    case 6 -> BANK.listAccounts().forEach(System.out::println);
                    case 7 -> System.out.println(BANK.report());
                    case 8 -> { saveAndExit(); running = false; }
                    default -> System.out.println("Please choose a number from 1 to 8.");
                }
            } catch (BankException | IOException e) { System.out.println("Operation failed: " + e.getMessage()); }
            catch (IllegalArgumentException e) { System.out.println("Invalid input: " + e.getMessage()); }
        }
    }
    private static void printMenu() {
        System.out.println("\n============== MINIBANK MENU ==============");
        System.out.println("1. Open account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. Account statement");
        System.out.println("6. List accounts (sorted by balance)");
        System.out.println("7. End-of-day report");
        System.out.println("8. Exit");
        System.out.println("===========================================");
    }
    private static void openAccount() throws BankException {
        String name = readText("Customer name: ");
        String type = readText("Type (S= Savings, C=Current, F=Fixed Deposit): ");
        long opening = readLong("Opening balance (₹): ");
        Account a = BANK.openAccount(name, type, opening);
        System.out.println("Account created successfully: " + a);
    }
    private static String readText(String prompt) {
        System.out.print(prompt); String value = SCANNER.nextLine().trim();
        if (value.isEmpty()) throw new IllegalArgumentException("This field cannot be empty.");
        return value;
    }
    private static int readInt(String prompt) {
        try { return Integer.parseInt(readText(prompt)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Enter a whole number."); }
    }
    private static long readLong(String prompt) {
        try { return Long.parseLong(readText(prompt)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Enter a whole-number rupee amount."); }
    }
    private static void saveAndExit() throws IOException {
        StatePersister.save(BANK.accountArray(), STATE_FILE);
        System.out.println("Account state saved. Thank you for using MiniBank.");
        try (CloseableDemo ignored = new CloseableDemo()) { System.out.println("Shutting down safely..."); }
    }

    // Generic bounded-type example required by the practical.
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) return 0.0;
        double sum = 0;
        for (N value : values) sum += value.doubleValue();
        return sum / values.length;
    }
}
