package com.example.errors;

/**
 * Exception class for Scanner errors.
 */
public class ScannerError extends RuntimeException {
  final int line;

  public ScannerError(int line, String message) {
    super(String.format("Line %d: %s", line, message));
    this.line = line;
  }
}
