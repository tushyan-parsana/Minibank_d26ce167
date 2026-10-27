package edu.charusat.minibank.util;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
public final class ReportGenerator {
    private ReportGenerator() {}
    public static String generate(Path logsDir, Path reportFile) throws IOException {
        long deposits = 0, withdrawals = 0;
        StringBuilder report = new StringBuilder("MINIBANK END-OF-DAY REPORT\n");
        if (Files.exists(logsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, "*.log")) {
                for (Path file : stream) {
                    BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                    report.append("\nFile: ").append(file.getFileName()).append(" | Size: ").append(attrs.size())
                          .append(" bytes | Modified: ").append(attrs.lastModifiedTime()).append('\n');
                    try (BufferedReader reader = Files.newBufferedReader(file)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            report.append(line).append('\n');
                            String upper = line.toUpperCase(Locale.ROOT);
                            try {
                                String[] parts = line.split("\\|");
                                String transaction = parts[parts.length - 1].trim().toUpperCase(Locale.ROOT);
                                String[] tokens = transaction.split("\\s+");
                                long amount = Long.parseLong(tokens[tokens.length - 1].replace("₹", ""));
                                if (upper.contains("DEPOSIT")) deposits += amount;
                                if (upper.contains("WITHDRAW")) withdrawals += amount;
                            } catch (RuntimeException ignored) { /* Skip non-standard log lines. */ }
                        }
                    }
                }
            }
        }
        report.append("\nTOTAL DEPOSITS: ₹").append(deposits).append("\nTOTAL WITHDRAWALS: ₹").append(withdrawals).append('\n');
        Path parent = reportFile.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(reportFile, report.toString());
        return report.toString();
    }
}
