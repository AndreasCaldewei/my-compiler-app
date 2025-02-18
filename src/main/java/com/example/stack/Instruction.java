package com.example.stack;

public class Instruction {
  public final String operation;
  public final Object operand;
  public final OperandType operandType;

  public Instruction(String operation, Object operand) {
    if (operation == null) {
      throw new IllegalArgumentException("Operation cannot be null");
    }
    this.operation = operation;
    this.operand = operand;
    this.operandType = OperandType.getType(operand);
  }

  // Konstruktor für Operationen ohne Operanden (wie ADD, SUB, etc.)
  public Instruction(String operation) {
    if (operation == null) {
      throw new IllegalArgumentException("Operation cannot be null");
    }
    this.operation = operation;
    this.operand = null;
    this.operandType = OperandType.NONE;
  }

  public boolean hasOperand() {
    return operandType != OperandType.NONE;
  }

  @Override
  public String toString() {
    if (hasOperand()) {
      return String.format("%s %s (%s)", operation, operand, operandType);
    } else {
      return operation;
    }
  }

  // Hilfsmethoden zur Type-Safety
  public double getNumberOperand() {
    if (operandType != OperandType.NUMBER) {
      throw new IllegalStateException("Operand is not a number, it is: " + operandType);
    }
    return ((Number) operand).doubleValue();
  }

  public String getStringOperand() {
    if (operandType != OperandType.STRING) {
      throw new IllegalStateException("Operand is not a string, it is: " + operandType);
    }
    return (String) operand;
  }

  public boolean getBooleanOperand() {
    if (operandType != OperandType.BOOLEAN) {
      throw new IllegalStateException("Operand is not a boolean, it is: " + operandType);
    }
    return (Boolean) operand;
  }

  public int getLabelOperand() {
    if (operandType != OperandType.LABEL) {
      throw new IllegalStateException("Operand is not a label, it is: " + operandType);
    }
    return (Integer) operand;
  }
}
