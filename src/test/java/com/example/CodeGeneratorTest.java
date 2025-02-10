package com.example;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.List;

public class CodeGeneratorTest {
  private CodeGenerator generator;

  @Before
  public void setUp() {
    generator = new CodeGenerator();
  }

  private List<Instruction> generateCode(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();
    return generator.generateCode(statements);
  }

  private void printInstructions(List<Instruction> instructions) {
    for (int i = 0; i < instructions.size(); i++) {
      System.out.printf("%4d: %s%n", i, instructions.get(i));
    }
  }

  @Test
  public void testSimpleArithmetic() {
    String source = "let x = 5 + 3;";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    assertEquals("PUSH", instructions.get(0).operation);
    assertEquals(5.0, ((Double) instructions.get(0).operand).doubleValue(), 0.001);
    assertEquals("PUSH", instructions.get(1).operation);
    assertEquals(3.0, ((Double) instructions.get(1).operand).doubleValue(), 0.001);
    assertEquals("ADD", instructions.get(2).operation);
    assertEquals("STORE", instructions.get(3).operation);
  }

  @Test
  public void testMultiplication() {
    String source = "let x = 4 * 2;";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    assertEquals("PUSH", instructions.get(0).operation);
    assertEquals("PUSH", instructions.get(1).operation);
    assertEquals("MUL", instructions.get(2).operation);
  }

  @Test
  public void testDivision() {
    String source = "let x = 10 / 2;";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    assertEquals("PUSH", instructions.get(0).operation);
    assertEquals("PUSH", instructions.get(1).operation);
    assertEquals("DIV", instructions.get(2).operation);
  }

  @Test
  public void testIfStatement() {
    String source = "if (x > 5) { let y = 1; } else { let y = 2; }";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    boolean hasJmpf = false;
    boolean hasJmp = false;
    for (Instruction inst : instructions) {
      if (inst.operation.equals("JMPF"))
        hasJmpf = true;
      if (inst.operation.equals("JMP"))
        hasJmp = true;
    }
    assertTrue("Should contain JMPF instruction", hasJmpf);
    assertTrue("Should contain JMP instruction", hasJmp);
  }

  @Test
  public void testWhileLoop() {
    String source = "while (x < 10) { x = x + 1; }";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    boolean hasLabel = false;
    boolean hasJmpf = false;
    boolean hasJmp = false;
    for (Instruction inst : instructions) {
      if (inst.operation.equals("LABEL"))
        hasLabel = true;
      if (inst.operation.equals("JMPF"))
        hasJmpf = true;
      if (inst.operation.equals("JMP"))
        hasJmp = true;
    }
    assertTrue("Should contain LABEL instruction", hasLabel);
    assertTrue("Should contain JMPF instruction", hasJmpf);
    assertTrue("Should contain JMP instruction", hasJmp);
  }

  @Test
  public void testFunctionDefinition() {
    String source = "function add(a, b) { return a + b; }";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    boolean hasStoreFun = false;
    boolean hasRet = false;
    for (Instruction inst : instructions) {
      if (inst.operation.equals("STOREFUN"))
        hasStoreFun = true;
      if (inst.operation.equals("RET"))
        hasRet = true;
    }
    assertTrue("Should contain STOREFUN instruction", hasStoreFun);
    assertTrue("Should contain RET instruction", hasRet);
  }

  @Test
  public void testVariableAssignment() {
    String source = "let x = 5; x = x + 1;";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    // Prüfen der ersten Zuweisung
    assertEquals("PUSH", instructions.get(0).operation);
    assertEquals("STORE", instructions.get(1).operation);

    // Prüfen der zweiten Zuweisung
    assertEquals("LOAD", instructions.get(2).operation);
    assertEquals("PUSH", instructions.get(3).operation);
    assertEquals("ADD", instructions.get(4).operation);
    assertEquals("STORE", instructions.get(5).operation);
  }

  @Test
  public void testComparison() {
    String source = "let result = 5 > 3;";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    assertEquals("PUSH", instructions.get(0).operation);
    assertEquals("PUSH", instructions.get(1).operation);
    assertEquals("GT", instructions.get(2).operation);
  }

  @Test
  public void testLogicalOperations() {
    String source = "let result = true && false;";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    assertEquals("PUSH", instructions.get(0).operation);
    assertEquals(true, instructions.get(0).operand);
    assertEquals("PUSH", instructions.get(1).operation);
    assertEquals(false, instructions.get(1).operand);
  }

  @Test
  public void testScope() {
    String source = "{ let x = 1; }";
    List<Instruction> instructions = generateCode(source);
    printInstructions(instructions);

    boolean hasBeginScope = false;
    boolean hasEndScope = false;
    for (Instruction inst : instructions) {
      if (inst.operation.equals("BEGINSCOPE"))
        hasBeginScope = true;
      if (inst.operation.equals("ENDSCOPE"))
        hasEndScope = true;
    }
    assertTrue("Should contain BEGINSCOPE instruction", hasBeginScope);
    assertTrue("Should contain ENDSCOPE instruction", hasEndScope);
  }
}
