package edu.charusat.minibank.util;
public class CloseableDemo implements AutoCloseable {
    @Override public void close() { System.out.println("Demo resource closed."); }
}
