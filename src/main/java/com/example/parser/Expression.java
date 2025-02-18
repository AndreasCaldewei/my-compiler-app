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

  abstract <R> R accept(Visitor<R> visitor);

  static class Assign extends Expression {
    final Token name;
    final Expression value;

    Assign(Token name, Expression value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpression(this);
    }
  }

  static class Binary extends Expression {
    final Expression left;
    final Token operator;
    final Expression right;

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

  static class Call extends Expression {
    final Expression callee;
    final Token paren;
    final List<Expression> arguments;

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

  static class Grouping extends Expression {
    final Expression expression;

    Grouping(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpression(this);
    }
  }

  static class Literal extends Expression {
    final Object value;

    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpression(this);
    }
  }

  static class Logical extends Expression {
    final Expression left;
    final Token operator;
    final Expression right;

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

  static class Unary extends Expression {
    final Token operator;
    final Expression right;

    Unary(Token operator, Expression right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpression(this);
    }
  }

  static class Variable extends Expression {
    final Token name;

    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpression(this);
    }
  }
}
