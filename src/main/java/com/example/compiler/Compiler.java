package com.example.compiler;

import java.util.List;

import com.example.code.CodeGenerator;
import com.example.parser.Parser;
import com.example.parser.Statement;
import com.example.scanner.Scanner;
import com.example.stack.StackMachine;
import com.example.scanner.Token;
import com.example.scanner.TokenType;
import com.example.code.Instruction;

public class Compiler {
  private boolean debug = false;

  public List<Instruction> generateCode(String source) {
    // Ensure the source ends with a semicolon
    if (!source.trim().endsWith(";")) {
      source = source.trim() + ";";
    }

    // Scan the source into tokens
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // Parse tokens into an Abstract Syntax Tree
    Parser parser = new Parser(tokens);
    List<Statement> statements = parser.parse();

    // Generate instructions
    CodeGenerator codeGenerator = new CodeGenerator();
    List<Instruction> instructions = codeGenerator.generateCode(statements);

    // Print instructions if debug is enabled
    if (debug) {
      printInstructions(instructions);
    }

    return instructions;
  }

  public void printInstructions(List<Instruction> instructions) {
    System.out.println("Generated Instructions:");
    for (int i = 0; i < instructions.size(); i++) {
      System.out.println(i + ": " + instructions.get(i));
    }
  }

  public Object execute(String source) {
    List<Instruction> instructions = generateCode(source);
    StackMachine stackMachine = new StackMachine(instructions);
    stackMachine.setDebug(debug);
    return stackMachine.execute();
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }
}
