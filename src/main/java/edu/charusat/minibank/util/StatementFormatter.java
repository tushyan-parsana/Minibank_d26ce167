package edu.charusat.minibank.util;
import edu.charusat.minibank.model.Account;
public final class StatementFormatter {
    private StatementFormatter() {}
    public static String buildStatement(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== MINIBANK ACCOUNT STATEMENT ==========\n")
          .append("Account Number : ").append(account.getAccountNumber()).append('\n')
          .append("Account Holder : ").append(account.getOwnerName()).append('\n')
          .append("Balance        : ₹").append(account.getBalance()).append('\n')
          .append("Interest Rate  : ").append(account.interestRate()).append("% p.a.\n")
          .append("Status         : ").append(account.isActive() ? "ACTIVE" : "INACTIVE").append('\n')
          .append("================================================");
        return sb.toString();
    }
}
