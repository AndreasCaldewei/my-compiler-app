package com.example;

import com.example.parser.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class ParserTest {
  private Parser parser;
  private List<Token> tokens;

  @Before
  public void setUp() {
    tokens = new ArrayList<>();
  }

  private void addToken(TokenType type) {
    tokens.add(new Token(type, "", null, 1));
  }

  private void addToken(TokenType type, Object literal) {
    tokens.add(new Token(type, literal.toString(), literal, 1));
  }

  private void addToken(TokenType type, String lexeme, Object literal) {
    tokens.add(new Token(type, lexeme, literal, 1));
  }

  @Test
  public void testEmptyProgram() {
    addToken(TokenType.EOF);
    parser = new Parser(tokens);
    List<Statement> statements = parser.parse();
    assertTrue(statements.isEmpty());
  }

  @Test
  public void testVariableDeclaration() {
    // let x = 42;
    addToken(TokenType.LET);
    addToken(TokenType.IDENTIFIER, "x");
    addToken(TokenType.EQUAL);
    addToken(TokenType.NUMBER, 42.0);
    addToken(TokenType.SEMICOLON);
    addToken(TokenType.EOF);

    parser = new Parser(tokens);
    List<Statement> statements = parser.parse();

    assertEquals(1, statements.size());
    assertTrue(statements.get(0) instanceof Statement.Var);
    Statement.Var varStmt = (Statement.Var) statements.get(0);
    assertEquals("x", varStmt.name.lexeme);
    assertTrue(varStmt.initializer instanceof Expression.Literal);
    assertEquals(42.0, ((Expression.Literal) varStmt.initializer).value);
  }

  @Test
  public void testConstDeclaration() {
    // const x = "hello";
    addToken(TokenType.CONST);
    addToken(TokenType.IDENTIFIER, "x");
    addToken(TokenType.EQUAL);
    addToken(TokenType.STRING, "hello");
    addToken(TokenType.SEMICOLON);
    addToken(TokenType.EOF);

    parser = new Parser(tokens);
    List<Statement> statements = parser.parse();

    assertEquals(1, statements.size());
    assertTrue(statements.get(0) instanceof Statement.Var);
    Statement.Var varStmt = (Statement.Var) statements.get(0);
    assertTrue(varStmt.isConst);
    assertEquals("hello", ((Expression.Literal) varStmt.initializer).value);
  }

  @Test
  public void testFunctionDeclaration() {
    // function add(x, y) {}
    addToken(TokenType.FUNCTION);
    addToken(TokenType.IDENTIFIER, "add");
    addToken(TokenType.LEFT_PAREN);
    addToken(TokenType.IDENTIFIER, "x");
    addToken(TokenType.COMMA);
    addToken(TokenType.IDENTIFIER, "y");
    addToken(TokenType.RIGHT_PAREN);
    addToken(TokenType.LEFT_BRACE);
    addToken(TokenType.RIGHT_BRACE);
    addToken(TokenType.EOF);

    parser = new Parser(tokens);
    List<Statement> statements = parser.parse();

    assertEquals(1, statements.size());
    assertTrue(statements.get(0) instanceof Statement.Function);
    Statement.Function funcStmt = (Statement.Function) statements.get(0);
    assertEquals("add", funcStmt.name.lexeme);
    assertEquals(2, funcStmt.params.size());
  }

  @Test
  public void testIfStatement() {
    // if (true) { let x = 1; }
    addToken(TokenType.IF);
    addToken(TokenType.LEFT_PAREN);
    addToken(TokenType.TRUE);
    addToken(TokenType.RIGHT_PAREN);
    addToken(TokenType.LEFT_BRACE);
    addToken(TokenType.LET);
    addToken(TokenType.IDENTIFIER, "x");
    addToken(TokenType.EQUAL);
    addToken(TokenType.NUMBER, 1.0);
    addToken(TokenType.SEMICOLON);
    addToken(TokenType.RIGHT_BRACE);
    addToken(TokenType.EOF);

    parser = new Parser(tokens);
    List<Statement> statements = parser.parse();

    assertEquals(1, statements.size());
    assertTrue(statements.get(0) instanceof Statement.If);
  }

  @Test
  public void testWhileLoop() {
    // while (true) {}
    addToken(TokenType.WHILE);
    addToken(TokenType.LEFT_PAREN);
    addToken(TokenType.TRUE);
    addToken(TokenType.RIGHT_PAREN);
    addToken(TokenType.LEFT_BRACE);
    addToken(TokenType.RIGHT_BRACE);
    addToken(TokenType.EOF);

    parser = new Parser(tokens);
    List<Statement> statements = parser.parse();

    assertEquals(1, statements.size());
    assertTrue(statements.get(0) instanceof Statement.While);
  }

  @Test(expected = ParseError.class)
  public void testMissingSemicolon() {
    addToken(TokenType.LET);
    addToken(TokenType.IDENTIFIER, "x");
    addToken(TokenType.EQUAL);
    addToken(TokenType.NUMBER, 42.0);
    // Missing semicolon
    addToken(TokenType.EOF);

    parser = new Parser(tokens);
    parser.parse();
  }

  @Test(expected = ParseError.class)
  public void testMissingInitializerForConst() {
    addToken(TokenType.CONST);
    addToken(TokenType.IDENTIFIER, "x");
    addToken(TokenType.SEMICOLON);
    addToken(TokenType.EOF);

    parser = new Parser(tokens);
    parser.parse();
  }

  @Test
  public void testComplexExpression() {
    // let x = (2 + 3) * 4;
    addToken(TokenType.LET);
    addToken(TokenType.IDENTIFIER, "x");
    addToken(TokenType.EQUAL);
    addToken(TokenType.LEFT_PAREN);
    addToken(TokenType.NUMBER, 2.0);
    addToken(TokenType.PLUS);
    addToken(TokenType.NUMBER, 3.0);
    addToken(TokenType.RIGHT_PAREN);
    addToken(TokenType.STAR);
    addToken(TokenType.NUMBER, 4.0);
    addToken(TokenType.SEMICOLON);
    addToken(TokenType.EOF);

    parser = new Parser(tokens);
    List<Statement> statements = parser.parse();

    assertEquals(1, statements.size());
    assertTrue(statements.get(0) instanceof Statement.Var);
    Statement.Var varStmt = (Statement.Var) statements.get(0);
    assertTrue(varStmt.initializer instanceof Expression.Binary);
  }
}
