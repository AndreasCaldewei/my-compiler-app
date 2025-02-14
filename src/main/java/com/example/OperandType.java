package com.example;

public enum OperandType {
  NONE, // Für Operationen ohne Operanden (z.B. ADD, SUB)
  NUMBER, // Für numerische Werte
  STRING, // Für Strings (z.B. Variablennamen)
  BOOLEAN, // Für boolesche Werte
  LABEL, // Für Sprungmarken
  FUNCTION; // Für Funktionsreferenzen

  public static OperandType getType(Object value) {
    if (value == null)
      return NONE;
    if (value instanceof Number)
      return NUMBER;
    if (value instanceof String)
      return STRING;
    if (value instanceof Boolean)
      return BOOLEAN;
    if (value instanceof Integer && value.toString().startsWith("L"))
      return LABEL;
    if (value instanceof Integer)
      return FUNCTION;
    throw new IllegalArgumentException("Unknown operand type: " + value.getClass());
  }
}
