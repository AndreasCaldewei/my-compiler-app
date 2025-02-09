package com.example;

import com.example.errors.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.List;

public class ScannerTest {
  private Scanner scanner;

  @Test
  public void testEmptyInput() {
    scanner = new Scanner("");
    List<Token> tokens = scanner.scanTokens();
    assertEquals(1, tokens.size());
    assertEquals(TokenType.EOF, tokens.get(0).type);
  }

  @Test
  public void testBasicString() {
    scanner = new Scanner("\"hello world\"");
    List<Token> tokens = scanner.scanTokens();
    assertEquals(2, tokens.size());
    assertEquals(TokenType.STRING, tokens.get(0).type);
    assertEquals("hello world", tokens.get(0).literal);
  }

  @Test
  public void testStringWithSimpleEscapes() {
    scanner = new Scanner("\"hello\\\"world\"");
    List<Token> tokens = scanner.scanTokens();
    assertEquals(2, tokens.size());
    assertEquals(TokenType.STRING, tokens.get(0).type);
    assertEquals("hello\"world", tokens.get(0).literal);
  }

  @Test
  public void testSingleCharacterTokens() {
    scanner = new Scanner("(){},.-+;*%");
    List<Token> tokens = scanner.scanTokens();
    TokenType[] expectedTypes = {
        TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN,
        TokenType.LEFT_BRACE, TokenType.RIGHT_BRACE,
        TokenType.COMMA, TokenType.DOT, TokenType.MINUS,
        TokenType.PLUS, TokenType.SEMICOLON, TokenType.STAR,
        TokenType.MODULO, TokenType.EOF
    };

    assertEquals(expectedTypes.length, tokens.size());
    for (int i = 0; i < expectedTypes.length; i++) {
      assertEquals(expectedTypes[i], tokens.get(i).type);
    }
  }

  @Test
  public void testTwoCharacterTokens() {
    scanner = new Scanner("!= == <= >= && ||");
    List<Token> tokens = scanner.scanTokens();
    TokenType[] expectedTypes = {
        TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL,
        TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL,
        TokenType.AND, TokenType.OR, TokenType.EOF
    };

    assertEquals(expectedTypes.length, tokens.size());
    for (int i = 0; i < expectedTypes.length; i++) {
      assertEquals(expectedTypes[i], tokens.get(i).type);
    }
  }

  @Test
  public void testNumbers() {
    scanner = new Scanner("123 456.789");
    List<Token> tokens = scanner.scanTokens();

    assertEquals(TokenType.NUMBER, tokens.get(0).type);
    assertEquals(123.0, tokens.get(0).literal);
    assertEquals(TokenType.NUMBER, tokens.get(1).type);
    assertEquals(456.789, tokens.get(1).literal);
  }

  @Test
  public void testKeywords() {
    scanner = new Scanner("function let const if else true false null while for return");
    List<Token> tokens = scanner.scanTokens();
    TokenType[] expectedTypes = {
        TokenType.FUNCTION, TokenType.LET, TokenType.CONST,
        TokenType.IF, TokenType.ELSE, TokenType.TRUE,
        TokenType.FALSE, TokenType.NULL, TokenType.WHILE,
        TokenType.FOR, TokenType.RETURN, TokenType.EOF
    };

    assertEquals(expectedTypes.length, tokens.size());
    for (int i = 0; i < expectedTypes.length; i++) {
      assertEquals(expectedTypes[i], tokens.get(i).type);
    }
  }

  @Test
  public void testIdentifiers() {
    scanner = new Scanner("foo bar baz123 _test");
    List<Token> tokens = scanner.scanTokens();

    for (int i = 0; i < tokens.size() - 1; i++) { // -1 to exclude EOF
      assertEquals(TokenType.IDENTIFIER, tokens.get(i).type);
    }
  }

  @Test
  public void testComments() {
    scanner = new Scanner("// This is a comment\n/* This is a\nmultiline comment */code");
    List<Token> tokens = scanner.scanTokens();

    assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
    assertEquals("code", tokens.get(0).lexeme);
  }

  @Test(expected = ScannerError.class)
  public void testUnterminatedString() {
    scanner = new Scanner("\"unterminated");
    scanner.scanTokens();
  }

  @Test(expected = ScannerError.class)
  public void testUnterminatedMultilineComment() {
    scanner = new Scanner("/* unterminated");
    scanner.scanTokens();
  }
}
