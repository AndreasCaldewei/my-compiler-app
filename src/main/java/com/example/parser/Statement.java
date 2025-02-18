package com.example.parser;

import java.util.List;

public abstract class Statement {
  public interface Visitor<R> {
    R visitBlockStmt(Block stmt);

    R visitExpressionessionStmt(Expressionession stmt);

    R visitFunctionStmt(Function stmt);

    R visitIfStmt(If stmt);

    R visitReturnStmt(Return stmt);

    R visitVarStmt(Var stmt);

    R visitWhileStmt(While stmt);
  }

  public abstract <R> R accept(Visitor<R> visitor);

  static class Block extends Statement {
    final List<Statement> statements;

    Block(List<Statement> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }
  }

  static class Expressionession extends Statement {
    final Expression expression;

    Expressionession(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionessionStmt(this);
    }
  }

  static class Function extends Statement {
    final Token name;
    final List<Token> params;
    final List<Statement> body;

    Function(Token name, List<Token> params, List<Statement> body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }
  }

  static class If extends Statement {
    final Expression condition;
    final Statement thenBranch;
    final Statement elseBranch;

    If(Expression condition, Statement thenBranch, Statement elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }
  }

  static class Return extends Statement {
    final Token keyword;
    final Expression value;

    Return(Token keyword, Expression value) {
      this.keyword = keyword;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }
  }

  static class Var extends Statement {
    final Token name;
    final Expression initializer;
    final boolean isConst;

    Var(Token name, Expression initializer, boolean isConst) {
      this.name = name;
      this.initializer = initializer;
      this.isConst = isConst;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }
  }

  static class While extends Statement {
    final Expression condition;
    final Statement body;

    While(Expression condition, Statement body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }
  }
}
