package com.example;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Assert;
import java.util.concurrent.TimeUnit;

public class CompilerTest {
  private Compiler compiler;

  @Before
  public void setUp() {
    compiler = new Compiler();
    // Uncomment to enable debug output during tests
    // compiler.setDebug(true);
  }

  @Test(timeout = 2000) // 2 seconds timeout
  public void testSimpleAddition() {
    Object result = compiler.execute("3 + 4;");
    assertNotNull("Result should not be null", result);
    assertEquals("Addition should work correctly", 7.0,
        ((Number) result).doubleValue(), 0.001);
  }

  @Test(timeout = 2000)
  public void testSimpleFunctionDefinitionAndCall() {
    Object result = compiler.execute(
        "function add(a, b) { return a + b; } " +
            "add(3, 4);");
    assertNotNull("Function call result should not be null", result);
    assertEquals("Function should return correct value", 7.0,
        ((Number) result).doubleValue(), 0.001);
  }

  @Test(timeout = 2000)
  public void testSimpleSubtraction() {
    Object result = compiler.execute("10 - 4;");
    assertNotNull("Result should not be null", result);
    assertEquals("Subtraction should work correctly", 6.0,
        ((Number) result).doubleValue(), 0.001);
  }

  @Test(timeout = 2000)
  public void testVariableDeclaration() {
    Object result = compiler.execute("let x = 5; x;");
    assertNotNull("Result should not be null", result);
    assertEquals("Variable declaration should work", 5.0,
        ((Number) result).doubleValue(), 0.001);
  }

  @Test(timeout = 2000)
  public void testLogicalOperations() {
    // AND operation
    Object andResult = compiler.execute("true && false;");
    assertNotNull("AND result should not be null", andResult);
    assertEquals("AND operation should work", false, andResult);

    // OR operation
    Object orResult = compiler.execute("true || false;");
    assertNotNull("OR result should not be null", orResult);
    assertEquals("OR operation should work", true, orResult);
  }

  @Test(timeout = 2000)
  public void testComparisonOperations() {
    // Greater than
    Object gtResult = compiler.execute("5 > 3;");
    assertTrue("Greater than should work", (Boolean) gtResult);

    // Less than or equal
    Object leResult = compiler.execute("3 <= 3;");
    assertTrue("Less than or equal should work", (Boolean) leResult);
  }

  @Test(timeout = 2000)
  public void testComplexExpression() {
    Object result = compiler.execute("(3 + 4) * 2;");
    assertNotNull("Result should not be null", result);
    assertEquals("Complex expression should work", 14.0,
        ((Number) result).doubleValue(), 0.001);
  }

  @Test(timeout = 2000)
  public void testMultipleStatements() {
    Object result = compiler.execute("let x = 5; let y = 3; x + y;");
    assertNotNull("Result should not be null", result);
    assertEquals("Multiple statements should work", 8.0,
        ((Number) result).doubleValue(), 0.001);
  }
}
