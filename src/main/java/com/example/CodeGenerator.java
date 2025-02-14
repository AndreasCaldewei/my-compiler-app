package com.example;

import java.util.*;

public class CodeGenerator implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
  private final List<Instruction> instructions = new ArrayList<>();
  private final Map<String, Integer> locals = new HashMap<>();
  private int labelCounter = 0;

  public List<Instruction> generateCode(List<Stmt> statements) {
    for (Stmt stmt : statements) {
      execute(stmt);
    }
    return instructions;
  }

  private void emit(String operation) {
    instructions.add(new Instruction(operation, null));
  }

  private void emit(String operation, Object operand) {
    instructions.add(new Instruction(operation, operand));
  }

  private int generateLabel() {
    return labelCounter++;
  }

  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  private void evaluate(Expr expr) {
    expr.accept(this);
  }

  @Override
  public Void visitBinaryExpr(Expr.Binary expr) {
    evaluate(expr.left);
    evaluate(expr.right);

    switch (expr.operator.type) {
      case PLUS:
        emit("ADD");
        break;
      case MINUS:
        emit("SUB");
        break;
      case STAR:
        emit("MUL");
        break;
      case SLASH:
        emit("DIV");
        break;
      case MODULO:
        emit("MOD");
        break;
      case EQUAL_EQUAL:
        emit("EQ");
        break;
      case BANG_EQUAL:
        emit("EQ");
        emit("NOT");
        break;
      case LESS:
        emit("LT");
        break;
      case GREATER:
        emit("GT");
        break;
      case LESS_EQUAL:
        emit("LE");
        break;
      case GREATER_EQUAL:
        emit("GE");
        break;
    }
    return null;
  }

  @Override
  public Void visitGroupingExpr(Expr.Grouping expr) {
    evaluate(expr.expression);
    return null;
  }

  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    emit("PUSH", expr.value);
    return null;
  }

  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    evaluate(expr.right);
    switch (expr.operator.type) {
      case MINUS:
        emit("NEG");
        break;
      case BANG:
        emit("NOT");
        break;
    }
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    emit("LOAD", expr.name.lexeme);
    return null;
  }

  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    evaluate(expr.value);
    emit("STORE", expr.name.lexeme);
    emit("LOAD", expr.name.lexeme); // Für Zuweisungsausdrücke wie a = b
    return null;
  }

  @Override
  public Void visitLogicalExpr(Expr.Logical expr) {
    evaluate(expr.left);
    evaluate(expr.right);

    if (expr.operator.type == TokenType.OR) {
      emit("OR");
    } else { // AND
      emit("AND");
    }

    return null;
  }

  @Override
  public Void visitCallExpr(Expr.Call expr) {
    // Argumente auswerten und auf den Stack legen
    for (Expr argument : expr.arguments) {
      evaluate(argument);
    }
    // Funktion aufrufen
    evaluate(expr.callee);
    emit("CALL", expr.arguments.size());
    return null;
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);
    emit("POP"); // Ergebnis verwerfen
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    int elseLabel = generateLabel();
    int endLabel = generateLabel();

    evaluate(stmt.condition);
    emit("JMPF", elseLabel);

    execute(stmt.thenBranch);
    emit("JMP", endLabel);

    emit("LABEL", elseLabel);
    if (stmt.elseBranch != null) {
      execute(stmt.elseBranch);
    }

    emit("LABEL", endLabel);
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    int startLabel = generateLabel();
    int endLabel = generateLabel();

    emit("LABEL", startLabel);
    evaluate(stmt.condition);
    emit("JMPF", endLabel);

    execute(stmt.body);
    emit("JMP", startLabel);

    emit("LABEL", endLabel);
    return null;
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    emit("BEGINSCOPE");
    for (Stmt statement : stmt.statements) {
      execute(statement);
    }
    emit("ENDSCOPE");
    return null;
  }

  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    int functionLabel = generateLabel();
    int afterFunction = generateLabel();

    // Springe über die Funktionsdefinition
    emit("JMP", afterFunction);

    // Funktionseintritt markieren
    emit("LABEL", functionLabel);
    emit("BEGINSCOPE");

    // Parameter auf lokale Variablen mappen
    int paramCount = stmt.params.size();
    for (int i = paramCount - 1; i >= 0; i--) {
      Token param = stmt.params.get(i);
      emit("STORE", param.lexeme);
    }

    // Funktionskörper
    for (Stmt statement : stmt.body) {
      execute(statement);
    }

    // Impliziter Return null wenn kein expliziter Return
    emit("PUSH", null);
    emit("RET");
    emit("ENDSCOPE");

    // Nach der Funktion fortfahren
    emit("LABEL", afterFunction);

    // Funktionsname an Adresse binden
    emit("PUSHFUN", functionLabel);
    emit("STOREFUN", stmt.name.lexeme);

    return null;
  }

  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    if (stmt.value != null) {
      evaluate(stmt.value);
    } else {
      emit("PUSH", null);
    }
    emit("RET");
    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    if (stmt.initializer != null) {
      evaluate(stmt.initializer);
    } else {
      emit("PUSH", null);
    }
    emit("STORE", stmt.name.lexeme);
    return null;
  }
}
