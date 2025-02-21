package com.example.parser;

import java.util.ArrayList;
import java.util.List;

import com.example.scanner.Token;
import com.example.scanner.TokenType;

public class Parser {
  private final List<Token> tokens;
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public List<Statement> parse() {
    List<Statement> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }
    return statements;
  }

  private Statement declaration() {
    try {
      if (match(TokenType.FUNCTION))
        return function();
      if (match(TokenType.LET))
        return varDeclaration(false);
      if (match(TokenType.CONST))
        return varDeclaration(true);

      return statement();
    } catch (ParseError error) {
      synchronize();
      throw error;
    }
  }

  private Statement function() {
    Token name = consume(TokenType.IDENTIFIER, "Expect function name.");
    consume(TokenType.LEFT_PAREN, "Expect '(' after function name.");

    List<Token> parameters = new ArrayList<>();
    if (!check(TokenType.RIGHT_PAREN)) {
      do {
        if (parameters.size() >= 255) {
          throw error(peek(), "Can't have more than 255 parameters.");
        }
        parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."));
      } while (match(TokenType.COMMA));
    }
    consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");

    consume(TokenType.LEFT_BRACE, "Expect '{' before function body.");
    List<Statement> body = block();
    return new Statement.Function(name, parameters, body);
  }

  private Statement varDeclaration(boolean isConst) {
    Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

    Expression initializer = null;
    if (match(TokenType.EQUAL)) {
      initializer = expression();
    } else if (isConst) {
      throw error(peek(), "Const declarations must be initialized.");
    }

    consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
    return new Statement.Var(name, initializer, isConst);
  }

  private Statement statement() {
    if (match(TokenType.IF))
      return ifStatement();
    if (match(TokenType.WHILE))
      return whileStatement();
    if (match(TokenType.FOR))
      return forStatement();
    if (match(TokenType.RETURN))
      return returnStatement();
    if (match(TokenType.LEFT_BRACE))
      return new Statement.Block(block());

    return expressionStatement();
  }

  private Statement ifStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
    Expression condition = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

    Statement thenBranch = statement();
    Statement elseBranch = null;
    if (match(TokenType.ELSE)) {
      elseBranch = statement();
    }

    return new Statement.If(condition, thenBranch, elseBranch);
  }

  private Statement whileStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
    Expression condition = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
    Statement body = statement();

    return new Statement.While(condition, body);
  }

  private Statement forStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");

    Statement initializer;
    if (match(TokenType.SEMICOLON)) {
      initializer = null;
    } else if (match(TokenType.LET)) {
      initializer = varDeclaration(false);
    } else {
      initializer = expressionStatement();
    }

    Expression condition = null;
    if (!check(TokenType.SEMICOLON)) {
      condition = expression();
    }
    consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

    Expression increment = null;
    if (!check(TokenType.RIGHT_PAREN)) {
      increment = expression();
    }
    consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");

    Statement body = statement();

    if (increment != null) {
      body = new Statement.Block(List.of(body, new Statement.Expression(increment)));
    }

    if (condition == null)
      condition = new Expression.Literal(true);
    body = new Statement.While(condition, body);

    if (initializer != null) {
      body = new Statement.Block(List.of(initializer, body));
    }

    return body;
  }

  private Statement returnStatement() {
    Token keyword = previous();
    Expression value = null;
    if (!check(TokenType.SEMICOLON)) {
      value = expression();
    }

    consume(TokenType.SEMICOLON, "Expect ';' after return value.");
    return new Statement.Return(keyword, value);
  }

  private List<Statement> block() {
    List<Statement> statements = new ArrayList<>();

    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
    return statements;
  }

  private Statement expressionStatement() {
    Expression expr = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after expression.");
    return new Statement.Expression(expr);
  }

  private Expression expression() {
    return assignment();
  }

  private Expression assignment() {
    Expression expr = logicalOr();

    if (match(TokenType.EQUAL)) {
      Token equals = previous();
      Expression value = assignment();

      if (expr instanceof Expression.Variable) {
        Token name = ((Expression.Variable) expr).name;
        return new Expression.Assign(name, value);
      }

      throw error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Expression logicalOr() {
    Expression expr = logicalAnd();

    while (match(TokenType.OR)) {
      Token operator = previous();
      Expression right = logicalAnd();
      expr = new Expression.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expression logicalAnd() {
    Expression expr = equality();

    while (match(TokenType.AND)) {
      Token operator = previous();
      Expression right = equality();
      expr = new Expression.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expression equality() {
    Expression expr = comparison();

    while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      Token operator = previous();
      Expression right = comparison();
      expr = new Expression.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression comparison() {
    Expression expr = term();

    while (match(TokenType.GREATER, TokenType.GREATER_EQUAL,
        TokenType.LESS, TokenType.LESS_EQUAL)) {
      Token operator = previous();
      Expression right = term();
      expr = new Expression.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression term() {
    Expression expr = factor();

    while (match(TokenType.MINUS, TokenType.PLUS)) {
      Token operator = previous();
      Expression right = factor();
      expr = new Expression.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression factor() {
    Expression expr = unary();
    while (match(TokenType.SLASH, TokenType.STAR, TokenType.MODULO)) {
      Token operator = previous();
      Expression right = unary();
      expr = new Expression.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression unary() {
    if (match(TokenType.BANG, TokenType.MINUS)) {
      Token operator = previous();
      Expression right = unary();
      return new Expression.Unary(operator, right);
    }

    return call();
  }

  private Expression call() {
    Expression expr = primary();

    while (true) {
      if (match(TokenType.LEFT_PAREN)) {
        expr = finishCall(expr);
      } else {
        break;
      }
    }

    return expr;
  }

  private Expression finishCall(Expression callee) {
    List<Expression> arguments = new ArrayList<>();
    if (!check(TokenType.RIGHT_PAREN)) {
      do {
        if (arguments.size() >= 255) {
          throw error(peek(), "Can't have more than 255 arguments.");
        }
        arguments.add(expression());
      } while (match(TokenType.COMMA));
    }

    Token paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");
    return new Expression.Call(callee, paren, arguments);
  }

  private Expression primary() {
    if (match(TokenType.FALSE))
      return new Expression.Literal(false);
    if (match(TokenType.TRUE))
      return new Expression.Literal(true);
    if (match(TokenType.NULL))
      return new Expression.Literal(null);

    if (match(TokenType.NUMBER, TokenType.STRING)) {
      return new Expression.Literal(previous().literal);
    }

    if (match(TokenType.IDENTIFIER)) {
      return new Expression.Variable(previous());
    }

    if (match(TokenType.LEFT_PAREN)) {
      Expression expr = expression();
      consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
      return new Expression.Grouping(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();
    throw error(peek(), message);
  }

  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd())
      current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == TokenType.EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private ParseError error(Token token, String message) {
    String errorMessage = message + " at " + token;
    System.err.println(errorMessage);
    throw new ParseError(errorMessage); // Changed to throw instead of return
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == TokenType.SEMICOLON)
        return;

      switch (peek().type) {
        case FUNCTION:
        case LET:
        case CONST:
        case IF:
        case WHILE:
        case FOR:
        case RETURN:
          return;
      }

      advance();
    }
  }
}
