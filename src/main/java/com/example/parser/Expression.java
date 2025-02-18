package com.example.parser;

import java.util.List;

public abstract class Expression {
  public interface Visitor<R> {
    R visitAssignExpression(Assign expr);

    R visitBinaryExpression(Binary expr);

    R visitCallExpression(Call expr);

    R visitGroupingExpression(Grouping expr);

    R visitLiteralExpression(Literal expr);

    R visitLogicalExpression(Logical expr);

    R visitUnaryExpression(Unary expr);

    R visitVariableExpression(Variable expr);
  }

  public abstract <R> R accept(Visitor<R> visitor);

  public static class Assign extends Expression {
    public final Token name;
    public final Expression value;

    Assign(Token name, Expression value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpression(this);
    }
  }

  public static class Binary extends Expression {
    public final Expression left;
    public final Token operator;
    public final Expression right;

    Binary(Expression left, Token operator, Expression right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpression(this);
    }
  }

  public static class Call extends Expression {
    public final Expression callee;
    final Token paren;
    public final List<Expression> arguments;

    Call(Expression callee, Token paren, List<Expression> arguments) {
      this.callee = callee;
      this.paren = paren;
      this.arguments = arguments;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpression(this);
    }
  }

  public static class Grouping extends Expression {
    public final Expression expression;

    Grouping(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpression(this);
    }
  }

  public static class Literal extends Expression {
    public final Object value;

    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpression(this);
    }
  }

  public static class Logical extends Expression {
    public final Expression left;
    public final Token operator;
    public final Expression right;

    Logical(Expression left, Token operator, Expression right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpression(this);
    }
  }

  public static class Unary extends Expression {
    public final Token operator;
    public final Expression right;

    Unary(Token operator, Expression right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpression(this);
    }
  }

  public static class Variable extends Expression {
    public Token name;

    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpression(this);
    }
  }
}
