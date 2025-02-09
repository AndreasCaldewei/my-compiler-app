package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();

  // Current position in source code
  private int start = 0; // Start of current token
  private int current = 0; // Current character being scanned
  private int line = 1; // Current line number

  // Keywords mapping
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("function", TokenType.FUNCTION);
    keywords.put("let", TokenType.LET);
    keywords.put("const", TokenType.CONST);
    keywords.put("if", TokenType.IF);
    keywords.put("else", TokenType.ELSE);
    keywords.put("true", TokenType.TRUE);
    keywords.put("false", TokenType.FALSE);
    keywords.put("null", TokenType.NULL);
    keywords.put("while", TokenType.WHILE);
    keywords.put("for", TokenType.FOR);
    keywords.put("return", TokenType.RETURN);
  }

  public Scanner(String source) {
    this.source = source;
  }

  /**
   * Scans all tokens in the source code.
   * 
   * @return List of scanned tokens
   */
  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      // Start of next lexeme
      start = current;
      scanToken();
    }

    // Add EOF token
    tokens.add(new Token(TokenType.EOF, "", null, line));
    return tokens;
  }

  /**
   * Scans a single token and adds it to the token list.
   */
  private void scanToken() {
    char c = advance();
    switch (c) {
      // Single-character tokens
      case '(':
        addToken(TokenType.LEFT_PAREN);
        break;
      case ')':
        addToken(TokenType.RIGHT_PAREN);
        break;
      case '{':
        addToken(TokenType.LEFT_BRACE);
        break;
      case '}':
        addToken(TokenType.RIGHT_BRACE);
        break;
      case '[':
        addToken(TokenType.LEFT_BRACKET);
        break;
      case ']':
        addToken(TokenType.RIGHT_BRACKET);
        break;
      case ',':
        addToken(TokenType.COMMA);
        break;
      case '.':
        addToken(TokenType.DOT);
        break;
      case '-':
        addToken(TokenType.MINUS);
        break;
      case '+':
        addToken(TokenType.PLUS);
        break;
      case ';':
        addToken(TokenType.SEMICOLON);
        break;
      case '*':
        addToken(TokenType.STAR);
        break;
      case '%':
        addToken(TokenType.MODULO);
        break;

      // Two-character tokens
      case '!':
        addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
        break;
      case '=':
        addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
        break;
      case '<':
        addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
        break;
      case '>':
        addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
        break;

      // Logical operators
      case '&':
        if (match('&')) {
          addToken(TokenType.AND);
        } else {
          throw new ScanError(line, "Unexpected character '&'");
        }
        break;
      case '|':
        if (match('|')) {
          addToken(TokenType.OR);
        } else {
          throw new ScanError(line, "Unexpected character '|'");
        }
        break;

      // Handle comments and division
      case '/':
        if (match('/')) {
          // Single-line comment
          while (peek() != '\n' && !isAtEnd())
            advance();
        } else if (match('*')) {
          // Multi-line comment
          multiLineComment();
        } else {
          addToken(TokenType.SLASH);
        }
        break;

      // Whitespace
      case ' ':
      case '\r':
      case '\t':
        break;

      case '\n':
        line++;
        break;

      // String literals
      case '"':
        string('"');
        break;
      case '\'':
        string('\'');
        break;

      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          throw new ScanError(line, "Unexpected character '" + c + "'");
        }
        break;
    }
  }

  /**
   * Handles string literals with either single or double quotes.
   */
  private void string(char quote) {
    StringBuilder value = new StringBuilder();

    while (peek() != quote && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
      } else if (peek() == '\\' && peekNext() == quote) {
        advance(); // consume backslash
        value.append(advance()); // append the quote
      } else {
        value.append(advance());
      }
    }

    if (isAtEnd()) {
      throw new ScanError(line, "Unterminated string.");
    }

    // Closing quote
    advance();

    // Get string value without quotes
    String text = value.toString();
    addToken(TokenType.STRING, text);
  }

  /**
   * Handles escape sequences in strings.
   */
  private void handleEscapeSequence() {
    char next = peek();
    switch (next) {
      case 'n':
      case 't':
      case 'r':
      case '\\':
      case '\'':
      case '"':
        advance();
        break;
      default:
        throw new ScanError(line, "Invalid escape sequence '\\" + next + "'");
    }
  }

  /**
   * Handles multi-line comments.
   */
  private void multiLineComment() {
    while (!isAtEnd() && !(peek() == '*' && peekNext() == '/')) {
      if (peek() == '\n')
        line++;
      advance();
    }

    if (isAtEnd()) {
      throw new ScanError(line, "Unterminated multi-line comment.");
    }

    // Consume the closing */
    advance(); // *
    advance(); // /
  }

  /**
   * Handles numeric literals (integers and decimals).
   */
  private void number() {
    while (isDigit(peek()))
      advance();

    // Look for decimal point
    if (peek() == '.' && isDigit(peekNext())) {
      advance(); // consume the dot
      while (isDigit(peek()))
        advance();
    }

    String numberStr = source.substring(start, current);
    Double value = Double.parseDouble(numberStr);
    addToken(TokenType.NUMBER, value);
  }

  /**
   * Handles identifiers and keywords.
   */
  private void identifier() {
    while (isAlphaNumeric(peek()))
      advance();

    String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    if (type == null)
      type = TokenType.IDENTIFIER;
    addToken(type);
  }

  // Helper methods
  private char advance() {
    return source.charAt(current++);
  }

  private boolean match(char expected) {
    if (isAtEnd() || source.charAt(current) != expected)
      return false;
    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd())
      return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length())
      return '\0';
    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
        c == '_';
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }
}

/**
 * Exception class for Scanner errors.
 */
class ScanError extends RuntimeException {
  final int line;

  ScanError(int line, String message) {
    super(String.format("Line %d: %s", line, message));
    this.line = line;
  }
}
