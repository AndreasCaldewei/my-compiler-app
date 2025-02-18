package com.example.stack;

import com.example.code.Operation;

public class Instruction {
  public final Operation operation;
  public final Object operand;
  public final OperandType operandType;

  public Instruction(Operation operation, Object operand) {
    if (operation == null) {
      throw new IllegalArgumentException("Operation cannot be null");
    }
    this.operation = operation;
    this.operand = operand;
    this.operandType = OperandType.getType(operand);
  }

  // Constructor for operations without operands (like ADD, SUB, etc.)
  public Instruction(Operation operation) {
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
      return operation.toString();
    }
  }

  // Helper methods for type safety
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
