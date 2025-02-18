package com.example.parser;

import java.util.List;

public abstract class Statement {
  public interface Visitor<R> {
    R visitBlockStmt(Block stmt);

    R visitExpressionStmt(Expression stmt);

    R visitFunctionStmt(Function stmt);

    R visitIfStmt(If stmt);

    R visitReturnStmt(Return stmt);

    R visitVarStmt(Var stmt);

    R visitWhileStmt(While stmt);
  }

  public abstract <R> R accept(Visitor<R> visitor);

  public static class Block extends Statement {
    final List<Statement> statements;

    Block(List<Statement> statements) {
      this.statements = statements;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }
  }

  public static class Expression extends Statement {
    final Expression expression;

    Expression(Expression expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }
  }

  public class Function extends Statement {
    final Token name;
    final List<Token> params;
    final List<Statement> body;

    Function(Token name, List<Token> params, List<Statement> body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }
  }

  public static class If extends Statement {
    public final Expression condition;
    public final Statement thenBranch;
    public final Statement elseBranch;

    If(Expression condition, Statement thenBranch, Statement elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }
  }

  public static class Return extends Statement {
    final Token keyword;
    final Expression value;

    Return(Token keyword, Expression value) {
      this.keyword = keyword;
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }
  }

  public static class Var extends Statement {
    final Token name;
    final Expression initializer;
    final boolean isConst;

    Var(Token name, Expression initializer, boolean isConst) {
      this.name = name;
      this.initializer = initializer;
      this.isConst = isConst;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }
  }

  public static class While extends Statement {
    public final Expression condition;
    public final Statement body;

    While(Expression condition, Statement body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }
  }

}
