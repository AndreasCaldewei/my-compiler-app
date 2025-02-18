package com.example.code;

public enum Operation {
  // Stack Operations
  PUSH, // Push a value onto the stack
  POP, // Remove top value from stack

  // Arithmetic Operations
  ADD, // Add top two values
  SUB, // Subtract top value from second value
  MUL, // Multiply top two values
  DIV, // Divide second value by top value
  MOD, // Modulo operation
  NEG, // Negate top value

  // Comparison Operations
  EQ, // Check if top two values are equal
  NOT, // Logical NOT of top value
  LT, // Less than comparison
  GT, // Greater than comparison
  LE, // Less than or equal comparison
  GE, // Greater than or equal comparison

  // Logical Operations
  AND, // Logical AND
  OR, // Logical OR

  // Variable Operations
  LOAD, // Load variable onto stack
  STORE, // Store top value in variable

  // Function Operations
  CALL, // Call function
  RET, // Return from function
  PUSHFUN, // Push function reference
  STOREFUN, // Store function reference

  // Scope Operations
  BEGINSCOPE, // Begin new scope
  ENDSCOPE, // End current scope

  // Control Flow
  JMP, // Unconditional jump
  JMPF, // Jump if false
  LABEL; // Define label for jumps

  @Override
  public String toString() {
    return name();
  }
}
