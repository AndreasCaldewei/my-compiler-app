package com.example;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class StackMachineTest {
  private StackMachine machine;
  private List<Instruction> instructions;

  @Before
  public void setUp() {
    instructions = new ArrayList<>();
  }

  @Test
  public void testAddOperation() {
    instructions.add(new Instruction("PUSH", 5.0)); // Erster Operand
    instructions.add(new Instruction("PUSH", 3.0)); // Zweiter Operand
    instructions.add(new Instruction("ADD")); // 5 + 3 sollte 8 ergeben

    machine = new StackMachine(instructions);
    machine.setDebug(true);
    machine.execute();

    assertEquals(8.0, machine.getNumberFromStack(), 0.001);
  }

  @Test
  public void testSimpleFunction() {
    // Function: add(a, b) { return a + b; }
    // Calling: add(5, 3) should return 8

    // Springe über die Funktionsdefinition
    instructions.add(new Instruction("JMP", 8));

    // Funktionsdefinition (ab Position 1)
    instructions.add(new Instruction("BEGINSCOPE"));
    instructions.add(new Instruction("STORE", "a")); // Ersten Parameter speichern (5.0)
    instructions.add(new Instruction("STORE", "b")); // Zweiten Parameter speichern (3.0)
    instructions.add(new Instruction("LOAD", "a")); // Lade 5.0
    instructions.add(new Instruction("LOAD", "b")); // Lade 3.0
    instructions.add(new Instruction("ADD")); // 5.0 + 3.0 = 8.0
    instructions.add(new Instruction("RET"));

    // Funktion im globalen Bereich speichern
    instructions.add(new Instruction("PUSHFUN", 1));
    instructions.add(new Instruction("STOREFUN", "add"));

    // Funktionsaufruf add(5, 3)
    instructions.add(new Instruction("PUSH", 5.0)); // Erster Parameter
    instructions.add(new Instruction("PUSH", 3.0)); // Zweiter Parameter
    instructions.add(new Instruction("PUSHFUN", 1));
    instructions.add(new Instruction("CALL", 2));

    machine = new StackMachine(instructions);

    // Debug-Ausgabe hinzufügen
    machine.setDebug(true);

    machine.execute();

    assertEquals(8.0, machine.getNumberFromStack(), 0.001);
  }

  @Test
  public void testBasicStackOperations() {
    instructions.add(new Instruction("PUSH", 42.0));
    instructions.add(new Instruction("DUP"));
    instructions.add(new Instruction("POP"));

    machine = new StackMachine(instructions);
    machine.execute();

    assertEquals(42.0, machine.getNumberFromStack(), 0.001);
  }

  @Test
  public void testArithmeticOperations() {
    // 3 + (4 * 2) = 11
    instructions.add(new Instruction("PUSH", 3.0)); // Push 3
    instructions.add(new Instruction("PUSH", 4.0)); // Push 4
    instructions.add(new Instruction("PUSH", 2.0)); // Push 2
    instructions.add(new Instruction("MUL")); // Multiply 4 * 2
    instructions.add(new Instruction("ADD")); // Add 3 + 8

    machine = new StackMachine(instructions);
    machine.execute();

    assertEquals(11.0, machine.getNumberFromStack(), 0.001);
  }

  @Test
  public void testVariableOperations() {
    instructions.add(new Instruction("PUSH", 5.0));
    instructions.add(new Instruction("STORE", "x"));
    instructions.add(new Instruction("LOAD", "x"));
    instructions.add(new Instruction("PUSH", 3.0));
    instructions.add(new Instruction("ADD"));
    instructions.add(new Instruction("STORE", "y"));
    instructions.add(new Instruction("LOAD", "y"));

    machine = new StackMachine(instructions);
    machine.execute();

    assertEquals(8.0, machine.getNumberFromStack(), 0.001);
  }

  @Test
  public void testScopeHandling() {
    instructions.add(new Instruction("PUSH", 1.0));
    instructions.add(new Instruction("STORE", "x")); // x = 1 in global scope

    instructions.add(new Instruction("BEGINSCOPE")); // Enter new scope
    instructions.add(new Instruction("PUSH", 2.0));
    instructions.add(new Instruction("STORE", "x")); // x = 2 in local scope
    instructions.add(new Instruction("LOAD", "x")); // Load x from local scope (should be 2)
    instructions.add(new Instruction("POP")); // Remove the 2 from stack
    instructions.add(new Instruction("ENDSCOPE")); // Exit local scope

    instructions.add(new Instruction("LOAD", "x")); // Load x from global scope (should be 1)

    machine = new StackMachine(instructions);
    machine.execute();

    assertEquals(1.0, machine.getNumberFromStack(), 0.001);
  }
}
