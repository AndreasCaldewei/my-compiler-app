package com.example.stack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import com.example.code.Operation;

public class StackMachine {
  private final Stack<Object> stack = new Stack<>();
  private final Stack<Integer> callStack = new Stack<>();
  private final Map<String, Object> globals = new HashMap<>();
  private final List<Map<String, Object>> scopes = new ArrayList<>();
  private final Map<String, Integer> functions = new HashMap<>();
  private final List<Instruction> instructions;
  private int ip = 0;
  private boolean debug = false;

  public StackMachine(List<Instruction> instructions) {
    this.instructions = instructions;
    scopes.add(new HashMap<>()); // Global scope
  }

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
      case PUSH:
        stack.push(instruction.operand);
        break;
      case POP:
        stack.pop();
        break;

      // Arithmetic operations
      case ADD: {
        ensureStackSize(2);
        debugPrint("Before ADD operation");
        Double addend = ((Number) stack.pop()).doubleValue();
        Double augend = ((Number) stack.pop()).doubleValue();
        Double result = augend + addend;
        debugPrint("ADD: " + augend + " + " + addend + " = " + result);
        stack.push(result);
        break;
      }
      case SUB: {
        ensureStackSize(2);
        Double b = ((Number) stack.pop()).doubleValue();
        Double a = ((Number) stack.pop()).doubleValue();
        stack.push(a - b);
        break;
      }
      case MUL: {
        ensureStackSize(2);
        Double b = ((Number) stack.pop()).doubleValue();
        Double a = ((Number) stack.pop()).doubleValue();
        stack.push(a * b);
        break;
      }
      case DIV: {
        ensureStackSize(2);
        Double b = ((Number) stack.pop()).doubleValue();
        Double a = ((Number) stack.pop()).doubleValue();
        if (b == 0)
          throw new RuntimeException("Division by zero");
        stack.push(a / b);
        break;
      }
      case MOD: {
        ensureStackSize(2);
        Double b = ((Number) stack.pop()).doubleValue();
        Double a = ((Number) stack.pop()).doubleValue();
        if (b == 0)
          throw new RuntimeException("Modulo by zero");
        stack.push(a % b);
        break;
      }
      case NEG: {
        ensureStackSize(1);
        Double a = ((Number) stack.pop()).doubleValue();
        stack.push(-a);
        break;
      }

      // Logical operations
      case NOT:
        ensureStackSize(1);
        stack.push(!isTruthy(stack.pop()));
        break;
      case EQ: {
        ensureStackSize(2);
        Object b = stack.pop();
        Object a = stack.pop();
        stack.push(Objects.equals(a, b));
        break;
      }
      case LT: {
        ensureStackSize(2);
        Double b = ((Number) stack.pop()).doubleValue();
        Double a = ((Number) stack.pop()).doubleValue();
        stack.push(a < b);
        break;
      }
      case GT: {
        ensureStackSize(2);
        Double b = ((Number) stack.pop()).doubleValue();
        Double a = ((Number) stack.pop()).doubleValue();
        stack.push(a > b);
        break;
      }
      case LE: {
        ensureStackSize(2);
        Double b = ((Number) stack.pop()).doubleValue();
        Double a = ((Number) stack.pop()).doubleValue();
        stack.push(a <= b);
        break;
      }
      case GE: {
        ensureStackSize(2);
        Double b = ((Number) stack.pop()).doubleValue();
        Double a = ((Number) stack.pop()).doubleValue();
        stack.push(a >= b);
        break;
      }

      // Variable operations
      case LOAD: {
        String name = instruction.getStringOperand();
        Object value = lookupVariable(name);
        stack.push(value);
        break;
      }
      case STORE: {
        ensureStackSize(1);
        String name = instruction.getStringOperand();
        Object value = stack.pop();
        storeVariable(name, value);
        debugPrint("STORE " + name + " = " + value);
        break;
      }

      // Control flow
      case JMP: {
        // TODO: This is really fixy
        int labelTarget = instruction.getLabelOperand();
        boolean found = false;
        for (int j = 0; j < instructions.size(); j++) {
          Instruction inst = instructions.get(j);
          if (inst.operation == Operation.LABEL && inst.getLabelOperand() == labelTarget) {
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
      case JMPF: {
        ensureStackSize(1);
        if (!isTruthy(stack.pop())) {
          ip = instruction.getLabelOperand() - 1;
        }
        break;
      }
      case LABEL:
        break;

      // Function operations
      case CALL: {
        ensureStackSize(1);
        Object callee = stack.pop();
        if (!(callee instanceof Integer)) {
          throw new RuntimeException("Can only call functions");
        }
        callStack.push(ip);
        ip = (Integer) callee;
        break;
      }
      case RET: {
        if (callStack.isEmpty()) {
          throw new RuntimeException("Return without a call");
        }
        ip = callStack.pop();
        break;
      }
      case PUSHFUN:
        stack.push(instruction.operand);
        break;
      case STOREFUN: {
        String name = instruction.getStringOperand();
        ensureStackSize(1);
        Integer address = (Integer) stack.pop();
        functions.put(name, address);
        break;
      }

      // Scope operations
      case BEGINSCOPE:
        scopes.add(new HashMap<>());
        break;
      case ENDSCOPE:
        if (scopes.size() <= 1) {
          throw new RuntimeException("Cannot end global scope");
        }
        scopes.remove(scopes.size() - 1);
        break;

      default:
        throw new RuntimeException("Unknown operation: " + instruction.operation);
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
    if (functions.containsKey(name)) {
      return functions.get(name);
    }

    for (int i = scopes.size() - 1; i >= 0; i--) {
      Map<String, Object> scope = scopes.get(i);
      if (scope.containsKey(name)) {
        return scope.get(name);
      }
    }

    if (globals.containsKey(name)) {
      return globals.get(name);
    }

    throw new RuntimeException("Undefined variable '" + name + "'.");
  }

  private void storeVariable(String name, Object value) {
    if (scopes.size() > 1) {
      Map<String, Object> currentScope = scopes.get(scopes.size() - 1);
      currentScope.put(name, value);
      return;
    }

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
