package com.example;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.example.code.Operation;
import com.example.stack.Instruction;
import com.example.stack.StackMachine;

public class StackMachineTest {
  private StackMachine stackMachine;
  private List<Instruction> instructions;

  @Before
  public void setUp() {
    instructions = new ArrayList<>();
  }

  @Test
  public void testBasicArithmeticOperations() {
    // Test addition
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 5.0));
    instructions.add(new Instruction(Operation.PUSH, 3.0));
    instructions.add(new Instruction(Operation.ADD, null));

    stackMachine = new StackMachine(instructions);
    stackMachine.setDebug(true);
    Object result = stackMachine.execute();

    assertEquals(8.0, result);

    // Test subtraction
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 10.0));
    instructions.add(new Instruction(Operation.PUSH, 4.0));
    instructions.add(new Instruction(Operation.SUB, null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(6.0, result);

    // Test multiplication
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 5.0));
    instructions.add(new Instruction(Operation.PUSH, 3.0));
    instructions.add(new Instruction(Operation.MUL, null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(15.0, result);

    // Test division
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 10.0));
    instructions.add(new Instruction(Operation.PUSH, 2.0));
    instructions.add(new Instruction(Operation.DIV, null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(5.0, result);
  }

  @Test(expected = RuntimeException.class)
  public void testDivisionByZero() {
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 10.0));
    instructions.add(new Instruction(Operation.PUSH, 0.0));
    instructions.add(new Instruction(Operation.DIV, null));

    stackMachine = new StackMachine(instructions);
    stackMachine.execute();
  }

  @Test
  public void testComparisonOperations() {
    // Test equality
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 5.0));
    instructions.add(new Instruction(Operation.PUSH, 5.0));
    instructions.add(new Instruction(Operation.EQ, null));

    stackMachine = new StackMachine(instructions);
    Object result = stackMachine.execute();

    assertEquals(true, result);

    // Test less than
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 3.0));
    instructions.add(new Instruction(Operation.PUSH, 5.0));
    instructions.add(new Instruction(Operation.LT, null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(true, result);

    // Test greater than
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 7.0));
    instructions.add(new Instruction(Operation.PUSH, 5.0));
    instructions.add(new Instruction(Operation.GT, null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(true, result);
  }

  @Test
  public void testVariableOperations() {
    // Test store and load
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 42.0));
    instructions.add(new Instruction(Operation.STORE, "x"));
    instructions.add(new Instruction(Operation.LOAD, "x"));

    stackMachine = new StackMachine(instructions);
    Object result = stackMachine.execute();

    assertEquals(42.0, result);
  }

  @Test
  public void testSimpleFunctionCall() {
    // Create a simple add function
    instructions.clear();
    instructions.add(new Instruction(Operation.JMP, 1)); // Skip function definition

    // Function definition (address 1)
    instructions.add(new Instruction(Operation.LABEL, 0));
    instructions.add(new Instruction(Operation.BEGINSCOPE, null));
    instructions.add(new Instruction(Operation.STORE, "b"));
    instructions.add(new Instruction(Operation.STORE, "a"));
    instructions.add(new Instruction(Operation.LOAD, "a"));
    instructions.add(new Instruction(Operation.LOAD, "b"));
    instructions.add(new Instruction(Operation.ADD, null));
    instructions.add(new Instruction(Operation.RET, null));
    instructions.add(new Instruction(Operation.ENDSCOPE, null));

    // Main program
    instructions.add(new Instruction(Operation.LABEL, 1));
    instructions.add(new Instruction(Operation.PUSHFUN, 1));
    instructions.add(new Instruction(Operation.STOREFUN, "add"));
    instructions.add(new Instruction(Operation.PUSH, 3.0));
    instructions.add(new Instruction(Operation.PUSH, 4.0));
    instructions.add(new Instruction(Operation.LOAD, "add"));
    instructions.add(new Instruction(Operation.CALL, 2));

    stackMachine = new StackMachine(instructions);
    stackMachine.setDebug(true);
    Object result = stackMachine.execute();

    assertEquals(7.0, result);
  }

  @Test
  public void testLogicalOperations() {
    // Test NOT
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, true));
    instructions.add(new Instruction(Operation.NOT, null));

    stackMachine = new StackMachine(instructions);
    Object result = stackMachine.execute();

    assertEquals(false, result);

    // Test complex logical combination
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 5.0));
    instructions.add(new Instruction(Operation.PUSH, 3.0));
    instructions.add(new Instruction(Operation.GT, null));
    instructions.add(new Instruction(Operation.PUSH, true));
    instructions.add(new Instruction(Operation.EQ, null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(true, result);
  }

  @Test
  public void testStackManipulation() {
    // Test stack operations
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 42.0));
    instructions.add(new Instruction(Operation.PUSH, 42.0));
    instructions.add(new Instruction(Operation.ADD, null));

    stackMachine = new StackMachine(instructions);
    Object result = stackMachine.execute();

    assertEquals(84.0, result);

    // Test POP
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 10.0));
    instructions.add(new Instruction(Operation.PUSH, 20.0));
    instructions.add(new Instruction(Operation.POP, null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(10.0, result);
  }

  @Test
  public void testScopeHandling() {
    // Test basic scope creation and variable shadowing
    instructions.clear();
    instructions.add(new Instruction(Operation.PUSH, 10.0));
    instructions.add(new Instruction(Operation.STORE, "x"));
    instructions.add(new Instruction(Operation.BEGINSCOPE, null));
    instructions.add(new Instruction(Operation.PUSH, 20.0));
    instructions.add(new Instruction(Operation.STORE, "x"));
    instructions.add(new Instruction(Operation.LOAD, "x"));
    instructions.add(new Instruction(Operation.ENDSCOPE, null));
    instructions.add(new Instruction(Operation.LOAD, "x"));

    stackMachine = new StackMachine(instructions);
    Object[] results = new Object[2];

    stackMachine.setDebug(true);
    for (int i = 0; i < 2; i++) {
      results[i] = stackMachine.peekStack();
      stackMachine.execute();
    }

    assertEquals(20.0, results[0]);
    assertEquals(10.0, results[1]);
  }

  @Test(expected = RuntimeException.class)
  public void testUndefinedVariableAccess() {
    instructions.clear();
    instructions.add(new Instruction(Operation.LOAD, "undefined_var"));

    stackMachine = new StackMachine(instructions);
    stackMachine.execute();
  }
}
