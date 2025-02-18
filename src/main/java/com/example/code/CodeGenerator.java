package com.example.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.parser.*;

public class CodeGenerator implements Expression.Visitor<Void>, Statement.Visitor<Void> {
  private final List<Instruction> instructions = new ArrayList<>();
  private int labelCounter = 0;

  public List<Instruction> generateCode(List<Statement> statements) {
    for (Statement stmt : statements) {
      execute(stmt);
    }
    return instructions;
  }

  private void emit(Operation operation) {
    instructions.add(new Instruction(operation, null));
  }

  private void emit(Operation operation, Object operand) {
    instructions.add(new Instruction(operation, operand));
  }

  private int generateLabel() {
    return labelCounter++;
  }

  private void execute(Statement stmt) {
    stmt.accept(this);
  }

  private void evaluate(Expression expr) {
    expr.accept(this);
  }

  @Override
  public Void visitBinaryExpression(Expression.Binary expr) {
    evaluate(expr.left);
    evaluate(expr.right);

    switch (expr.operator.type) {
      case PLUS:
        emit(Operation.ADD);
        break;
      case MINUS:
        emit(Operation.SUB);
        break;
      case STAR:
        emit(Operation.MUL);
        break;
      case SLASH:
        emit(Operation.DIV);
        break;
      case MODULO:
        emit(Operation.MOD);
        break;
      case EQUAL_EQUAL:
        emit(Operation.EQ);
        break;
      case BANG_EQUAL:
        emit(Operation.EQ);
        emit(Operation.NOT);
        break;
      case LESS:
        emit(Operation.LT);
        break;
      case GREATER:
        emit(Operation.GT);
        break;
      case LESS_EQUAL:
        emit(Operation.LE);
        break;
      case GREATER_EQUAL:
        emit(Operation.GE);
        break;
    }
    return null;
  }

  @Override
  public Void visitGroupingExpression(Expression.Grouping expr) {
    evaluate(expr.expression);
    return null;
  }

  @Override
  public Void visitLiteralExpression(Expression.Literal expr) {
    emit(Operation.PUSH, expr.value);
    return null;
  }

  @Override
  public Void visitUnaryExpression(Expression.Unary expr) {
    evaluate(expr.right);
    switch (expr.operator.type) {
      case MINUS:
        emit(Operation.NEG);
        break;
      case BANG:
        emit(Operation.NOT);
        break;
    }
    return null;
  }

  @Override
  public Void visitVariableExpression(Expression.Variable expr) {
    emit(Operation.LOAD, expr.name.lexeme);
    return null;
  }

  @Override
  public Void visitAssignExpression(Expression.Assign expr) {
    evaluate(expr.value);
    emit(Operation.STORE, expr.name.lexeme);
    emit(Operation.LOAD, expr.name.lexeme);
    return null;
  }

  @Override
  public Void visitLogicalExpression(Expression.Logical expr) {
    evaluate(expr.left);
    evaluate(expr.right);

    if (expr.operator.type == TokenType.OR) {
      emit(Operation.OR);
    } else { // AND
      emit(Operation.AND);
    }

    return null;
  }

  @Override
  public Void visitCallExpression(Expression.Call expr) {
    for (Expression argument : expr.arguments) {
      evaluate(argument);
    }
    evaluate(expr.callee);
    emit(Operation.CALL, expr.arguments.size());
    return null;
  }

  @Override
  public Void visitExpressionStmt(Statement.Expression stmt) {
    evaluate(stmt.expression);
    emit(Operation.POP);
    return null;
  }

  @Override
  public Void visitIfStmt(Statement.If stmt) {
    int elseLabel = generateLabel();
    int endLabel = generateLabel();

    evaluate(stmt.condition);
    emit(Operation.JMPF, elseLabel);

    execute(stmt.thenBranch);
    emit(Operation.JMP, endLabel);

    emit(Operation.LABEL, elseLabel);
    if (stmt.elseBranch != null) {
      execute(stmt.elseBranch);
    }

    emit(Operation.LABEL, endLabel);
    return null;
  }

  @Override
  public Void visitWhileStmt(Statement.While stmt) {
    int startLabel = generateLabel();
    int endLabel = generateLabel();

    emit(Operation.LABEL, startLabel);
    evaluate(stmt.condition);
    emit(Operation.JMPF, endLabel);

    execute(stmt.body);
    emit(Operation.JMP, startLabel);

    emit(Operation.LABEL, endLabel);
    return null;
  }

  @Override
  public Void visitBlockStmt(Statement.Block stmt) {
    emit(Operation.BEGINSCOPE);
    for (Statement statement : stmt.statements) {
      execute(statement);
    }
    emit(Operation.ENDSCOPE);
    return null;
  }

  @Override
  public Void visitFunctionStmt(Statement.Function stmt) {
    int functionLabel = generateLabel();
    int afterFunction = generateLabel();

    emit(Operation.JMP, afterFunction);

    emit(Operation.LABEL, functionLabel);
    emit(Operation.BEGINSCOPE);

    int paramCount = stmt.params.size();
    for (int i = paramCount - 1; i >= 0; i--) {
      Token param = stmt.params.get(i);
      emit(Operation.STORE, param.lexeme);
    }

    for (Statement statement : stmt.body) {
      execute(statement);
    }

    emit(Operation.PUSH, null);
    emit(Operation.RET);
    emit(Operation.ENDSCOPE);

    emit(Operation.LABEL, afterFunction);

    emit(Operation.PUSHFUN, functionLabel);
    emit(Operation.STOREFUN, stmt.name.lexeme);

    return null;
  }

  @Override
  public Void visitReturnStmt(Statement.Return stmt) {
    if (stmt.value != null) {
      evaluate(stmt.value);
    } else {
      emit(Operation.PUSH, null);
    }
    emit(Operation.RET);
    return null;
  }

  @Override
  public Void visitVarStmt(Statement.Var stmt) {
    if (stmt.initializer != null) {
      evaluate(stmt.initializer);
    } else {
      emit(Operation.PUSH, null);
    }
    emit(Operation.STORE, stmt.name.lexeme);
    return null;
  }
}
