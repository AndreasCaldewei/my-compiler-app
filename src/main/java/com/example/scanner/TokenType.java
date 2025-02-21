package com.example.scanner;

public enum TokenType {
  // Single-character tokens
  LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
  LEFT_BRACKET, RIGHT_BRACKET, COMMA, DOT, MINUS, PLUS,
  SEMICOLON, SLASH, STAR, MODULO,

  // One or two character tokens
  BANG, BANG_EQUAL,
  EQUAL, EQUAL_EQUAL,
  GREATER, GREATER_EQUAL,
  LESS, LESS_EQUAL,
  AND, OR,

  // Literals
  IDENTIFIER, STRING, NUMBER,

  // Keywords
  FUNCTION, LET, CONST, IF, ELSE, TRUE, FALSE,
  NULL, WHILE, FOR, RETURN,

  EOF
}
