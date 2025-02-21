package com.example.parser;

import com.example.scanner.*;
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
    public final List<Statement> statements;

    public Block(List<Statement> statements) {
      this.statements = statements;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }
  }

  public static class Expression extends Statement {
    public final com.example.parser.Expression expression;

    public Expression(com.example.parser.Expression expression) {
      this.expression = expression;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }
  }

  public static class Function extends Statement {
    public final Token name;
    public final List<Token> params;
    public final List<Statement> body;

    public Function(Token name, List<Token> params, List<Statement> body) {
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
    public final com.example.parser.Expression condition;
    public final Statement thenBranch;
    public final Statement elseBranch;

    public If(com.example.parser.Expression condition, Statement thenBranch, Statement elseBranch) {
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
    public final Token keyword;
    public final com.example.parser.Expression value;

    public Return(Token keyword, com.example.parser.Expression value) {
      this.keyword = keyword;
      this.value = value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }
  }

  public static class Var extends Statement {
    public final Token name;
    public final com.example.parser.Expression initializer;
    public final boolean isConst;

    public Var(Token name, com.example.parser.Expression initializer, boolean isConst) {
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
    public final com.example.parser.Expression condition;
    public final Statement body;

    public While(com.example.parser.Expression condition, Statement body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }
  }
}
