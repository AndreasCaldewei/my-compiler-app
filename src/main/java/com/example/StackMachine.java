package com.example;

import java.util.*;

public class StackMachine {
  private final Stack<Object> stack = new Stack<>();
  private final Stack<Integer> callStack = new Stack<>(); // Neuer Stack für Rücksprungadressen
  private final Map<String, Object> globals = new HashMap<>();
  private final List<Map<String, Object>> scopes = new ArrayList<>();
  private final Map<String, Integer> functions = new HashMap<>();
  private final List<Instruction> instructions;
  private int ip = 0;

  public StackMachine(List<Instruction> instructions) {
    this.instructions = instructions;
    scopes.add(new HashMap<>()); // Global scope
  }

  private boolean debug = false;

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  private void debugPrint(String message) {
    if (debug) {
      System.out.println("[DEBUG] " + message);
      dumpStack();
    }
  }

  public Object execute() {
    Object lastValue = null;
    int maxIterations = instructions.size() * 2; // Prevent infinite loops
    int iterationCount = 0;

    while (ip < instructions.size() && iterationCount < maxIterations) {
      Instruction instruction = instructions.get(ip);
      if (debug) {
        System.out.println("\nExecuting instruction at IP=" + ip + ": " + instruction);
      }
      executeInstruction(instruction);

      // If stack is not empty after instruction, update lastValue
      if (!stack.isEmpty()) {
        lastValue = stack.peek();
      }

      ip++;
      iterationCount++;
    }

    if (iterationCount >= maxIterations) {
      throw new RuntimeException("Possible infinite loop detected");
    }

    return lastValue;
  }

  private void ensureStackSize(int required) {
    if (stack.size() < required) {
      throw new RuntimeException("Stack underflow: required " + required + " elements but got " + stack.size());
    }
  }

  private void executeInstruction(Instruction instruction) {
    debugPrint("Before executing " + instruction);

    switch (instruction.operation) {
      // Stack operations
      case "PUSH":
        stack.push(instruction.operand);
        break;
      case "POP":
        stack.pop();
        break;
      case "DUP":
        stack.push(stack.peek());
        break;

      // Arithmetic operations
      case "ADD": {
        ensureStackSize(2);
        debugPrint("Before ADD operation");

        // Pop in richtiger Reihenfolge
        Double addend = ((Number) stack.pop()).doubleValue(); // Zweiter Operand
        Double augend = ((Number) stack.pop()).doubleValue(); // Erster Operand
        Double result = augend + addend;

        debugPrint("ADD: " + augend + " + " + addend + " = " + result);
        stack.push(result);
        break;
      }
      case "SUB": {
        Double b = (Double) stack.pop();
        Double a = (Double) stack.pop();
        stack.push(a - b);
        break;
      }
      case "MUL": {
        Double b = (Double) stack.pop();
        Double a = (Double) stack.pop();
        stack.push(a * b);
        break;
      }
      case "DIV": {
        Double b = (Double) stack.pop();
        Double a = (Double) stack.pop();
        if (b == 0)
          throw new RuntimeException("Division by zero");
        stack.push(a / b);
        break;
      }
      case "MOD": {
        Double b = (Double) stack.pop();
        Double a = (Double) stack.pop();
        if (b == 0)
          throw new RuntimeException("Modulo by zero");
        stack.push(a % b);
        break;
      }
      case "NEG": {
        Double a = (Double) stack.pop();
        stack.push(-a);
        break;
      }

      // Logical operations
      case "NOT":
        stack.push(!isTruthy(stack.pop()));
        break;
      case "EQ": {
        Object b = stack.pop();
        Object a = stack.pop();
        stack.push(Objects.equals(a, b));
        break;
      }
      case "LT": {
        Double b = (Double) stack.pop();
        Double a = (Double) stack.pop();
        stack.push(a < b);
        break;
      }
      case "GT": {
        Double b = (Double) stack.pop();
        Double a = (Double) stack.pop();
        stack.push(a > b);
        break;
      }
      case "LE": {
        Double b = (Double) stack.pop();
        Double a = (Double) stack.pop();
        stack.push(a <= b);
        break;
      }
      case "GE": {
        Double b = (Double) stack.pop();
        Double a = (Double) stack.pop();
        stack.push(a >= b);
        break;
      }

      // Variable operations
      case "LOAD": {
        String name = (String) instruction.operand;
        Object value = lookupVariable(name);
        stack.push(value);
        break;
      }
      case "STORE": {
        ensureStackSize(1);
        String name = (String) instruction.operand;
        Object value = stack.pop(); // Wert vom Stack nehmen
        storeVariable(name, value);
        debugPrint("STORE " + name + " = " + value);
        break;
      }

      // Control flow
      case "JMP": {
        Integer labelTarget = (Integer) instruction.operand;
        boolean found = false;
        for (int j = 0; j < instructions.size(); j++) {
          Instruction inst = instructions.get(j);
          if (inst.operation.equals("LABEL") && inst.operand.equals(labelTarget)) {
            ip = j;
            found = true;
            break;
          }
        }
        if (!found) {
          throw new RuntimeException("Label " + labelTarget + " not found");
        }
        break;
      }
      case "JMPF":
        if (!isTruthy(stack.pop())) {
          ip = (Integer) instruction.operand - 1;
        }
        break;
      case "JMPT":
        if (scopes.size() > 1) {
          ip = (Integer) instruction.operand - 1;
        }
        break;
      case "LABEL":
        break;

      // Function operations
      case "CALL": {
        Object callee = stack.pop();
        if (!(callee instanceof Integer)) {
          throw new RuntimeException("Can only call functions");
        }

        // Save the current instruction pointer on the call stack
        // Use a negative offset to prevent re-executing the same sequence
        callStack.push(ip + 1);

        // Jump to the function's start
        ip = (Integer) callee - 1;
        break;
      }

      case "RET": {
        // Pop the return value from the stack
        Object returnValue = stack.pop();

        // Restore the instruction pointer from the call stack
        if (callStack.isEmpty()) {
          throw new RuntimeException("Return without a call");
        }
        ip = callStack.pop();

        // Push the return value back onto the stack
        stack.push(returnValue);
        break;
      }
      case "PUSHFUN":
        stack.push(instruction.operand);
        break;
      case "STOREFUN": {
        String name = (String) instruction.operand;
        Integer address = (Integer) stack.pop();
        functions.put(name, address);
        break;
      }

      // Scope handling
      case "BEGINSCOPE":
        scopes.add(new HashMap<>());
        break;
      case "ENDSCOPE":
        if (scopes.size() <= 1) {
          throw new RuntimeException("Cannot end global scope");
        }
        scopes.remove(scopes.size() - 1);
        break;
    }
  }

  private boolean isTruthy(Object object) {
    if (object == null)
      return false;
    if (object instanceof Boolean)
      return (Boolean) object;
    return true;
  }

  private Object lookupVariable(String name) {
    // First, check if it's a function
    if (functions.containsKey(name)) {
      return functions.get(name);
    }

    // Then check scopes
    for (int i = scopes.size() - 1; i >= 0; i--) {
      Map<String, Object> scope = scopes.get(i);
      if (scope.containsKey(name)) {
        return scope.get(name);
      }
    }

    // Then check globals
    if (globals.containsKey(name)) {
      return globals.get(name);
    }

    throw new RuntimeException("Undefined variable '" + name + "'.");
  }

  private void storeVariable(String name, Object value) {
    // In einem neuen Scope wird eine neue Variable erstellt
    if (scopes.size() > 1) {
      Map<String, Object> currentScope = scopes.get(scopes.size() - 1);
      currentScope.put(name, value);
      return;
    }

    // Im globalen Scope überprüfen wir, ob die Variable existiert
    Map<String, Object> globalScope = scopes.get(0);
    globalScope.put(name, value);
  }

  public void dumpStack() {
    System.out.println("Stack: " + stack);
    System.out.println("Call Stack: " + callStack);
    System.out.println("Globals: " + globals);
    System.out.println("Scopes: " + scopes);
    System.out.println("IP: " + ip);
  }

  public Object peekStack() {
    ensureStackSize(1);
    return stack.peek();
  }

  public double getNumberFromStack() {
    Object value = peekStack();
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    throw new RuntimeException("Top of stack is not a number: " + value);
  }
}
