package com.example;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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
    instructions.add(new Instruction("PUSH", 5.0));
    instructions.add(new Instruction("PUSH", 3.0));
    instructions.add(new Instruction("ADD", null));

    stackMachine = new StackMachine(instructions);
    stackMachine.setDebug(true);
    Object result = stackMachine.execute();

    assertEquals(8.0, result);

    // Test subtraction
    instructions.clear();
    instructions.add(new Instruction("PUSH", 10.0));
    instructions.add(new Instruction("PUSH", 4.0));
    instructions.add(new Instruction("SUB", null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(6.0, result);

    // Test multiplication
    instructions.clear();
    instructions.add(new Instruction("PUSH", 5.0));
    instructions.add(new Instruction("PUSH", 3.0));
    instructions.add(new Instruction("MUL", null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(15.0, result);

    // Test division
    instructions.clear();
    instructions.add(new Instruction("PUSH", 10.0));
    instructions.add(new Instruction("PUSH", 2.0));
    instructions.add(new Instruction("DIV", null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(5.0, result);
  }

  @Test(expected = RuntimeException.class)
  public void testDivisionByZero() {
    instructions.clear();
    instructions.add(new Instruction("PUSH", 10.0));
    instructions.add(new Instruction("PUSH", 0.0));
    instructions.add(new Instruction("DIV", null));

    stackMachine = new StackMachine(instructions);
    stackMachine.execute();
  }

  @Test
  public void testComparisonOperations() {
    // Test equality
    instructions.clear();
    instructions.add(new Instruction("PUSH", 5.0));
    instructions.add(new Instruction("PUSH", 5.0));
    instructions.add(new Instruction("EQ", null));

    stackMachine = new StackMachine(instructions);
    Object result = stackMachine.execute();

    assertEquals(true, result);

    // Test less than
    instructions.clear();
    instructions.add(new Instruction("PUSH", 3.0));
    instructions.add(new Instruction("PUSH", 5.0));
    instructions.add(new Instruction("LT", null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(true, result);

    // Test greater than
    instructions.clear();
    instructions.add(new Instruction("PUSH", 7.0));
    instructions.add(new Instruction("PUSH", 5.0));
    instructions.add(new Instruction("GT", null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(true, result);
  }

  @Test
  public void testVariableOperations() {
    // Test store and load
    instructions.clear();
    instructions.add(new Instruction("PUSH", 42.0));
    instructions.add(new Instruction("STORE", "x"));
    instructions.add(new Instruction("LOAD", "x"));

    stackMachine = new StackMachine(instructions);
    Object result = stackMachine.execute();

    assertEquals(42.0, result);
  }

  @Test
  public void testSimpleFunctionCall() {
    // Create a simple add function
    instructions.clear();
    instructions.add(new Instruction("JMP", 1)); // Skip function definition

    // Function definition (address 1)
    instructions.add(new Instruction("LABEL", 0));
    instructions.add(new Instruction("BEGINSCOPE"));
    instructions.add(new Instruction("STORE", "b"));
    instructions.add(new Instruction("STORE", "a"));
    instructions.add(new Instruction("LOAD", "a"));
    instructions.add(new Instruction("LOAD", "b"));
    instructions.add(new Instruction("ADD"));
    instructions.add(new Instruction("RET"));
    instructions.add(new Instruction("ENDSCOPE"));

    // Main program (address 10)
    instructions.add(new Instruction("LABEL", 1));
    instructions.add(new Instruction("PUSHFUN", 1)); // Function address
    instructions.add(new Instruction("STOREFUN", "add"));
    instructions.add(new Instruction("PUSH", 3.0));
    instructions.add(new Instruction("PUSH", 4.0));
    instructions.add(new Instruction("LOAD", "add"));
    instructions.add(new Instruction("CALL", 2));

    stackMachine = new StackMachine(instructions);
    stackMachine.setDebug(true);
    Object result = stackMachine.execute();

    assertEquals(7.0, result);
  }

  @Test
  public void testLogicalOperations() {
    // Test NOT
    instructions.clear();
    instructions.add(new Instruction("PUSH", true));
    instructions.add(new Instruction("NOT", null));

    stackMachine = new StackMachine(instructions);
    Object result = stackMachine.execute();

    assertEquals(false, result);

    // Test complex logical combination
    instructions.clear();
    instructions.add(new Instruction("PUSH", 5.0));
    instructions.add(new Instruction("PUSH", 3.0));
    instructions.add(new Instruction("GT", null));
    instructions.add(new Instruction("PUSH", true));
    instructions.add(new Instruction("EQ", null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(true, result);
  }

  @Test
  public void testStackManipulation() {
    // Test DUP
    instructions.clear();
    instructions.add(new Instruction("PUSH", 42.0));
    instructions.add(new Instruction("DUP", null));
    instructions.add(new Instruction("ADD", null));

    stackMachine = new StackMachine(instructions);
    Object result = stackMachine.execute();

    assertEquals(84.0, result);

    // Test POP
    instructions.clear();
    instructions.add(new Instruction("PUSH", 10.0));
    instructions.add(new Instruction("PUSH", 20.0));
    instructions.add(new Instruction("POP", null));

    stackMachine = new StackMachine(instructions);
    result = stackMachine.execute();

    assertEquals(10.0, result);
  }

  @Test
  public void testScopeHandling() {
    // Test basic scope creation and variable shadowing
    instructions.clear();
    instructions.add(new Instruction("PUSH", 10.0));
    instructions.add(new Instruction("STORE", "x"));
    instructions.add(new Instruction("BEGINSCOPE"));
    instructions.add(new Instruction("PUSH", 20.0));
    instructions.add(new Instruction("STORE", "x"));
    instructions.add(new Instruction("LOAD", "x"));
    instructions.add(new Instruction("ENDSCOPE"));
    instructions.add(new Instruction("LOAD", "x"));

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
    instructions.add(new Instruction("LOAD", "undefined_var"));

    stackMachine = new StackMachine(instructions);
    stackMachine.execute();
  }
}
