package com.example;

public class Instruction {
  public final String operation;
  public final Object operand;

  public Instruction(String operation, Object operand) {
    this.operation = operation;
    this.operand = operand;
  }

  @Override
  public String toString() {
    return operation + (operand != null ? " " + operand : "");
  }
}
