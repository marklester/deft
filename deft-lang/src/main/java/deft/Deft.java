package deft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import deft.grammar.Token;
import deft.grammar.TokenType;
import deft.grammar.generated.Expression;
import deft.lang.Interpreter;
import deft.lang.Parser;
import deft.lang.Scanner;
import deft.lang.errors.RuntimeError;

public class Deft {
  static boolean hadError = false;
  static boolean hadRuntimeError=false;
  private static final Interpreter intepreter = new Interpreter();

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: deft [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    for (;;) {
      System.out.println("> ");
      run(reader.readLine());
      hadError = false;
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, StandardCharsets.UTF_8));
    if (hadError)
      System.exit(65);
    if (hadRuntimeError) System.exit(70);
  }

  private static void run(String string) {
    Scanner scanner = new Scanner(string);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    Expression expression = parser.parse();

    // Stop if there was a syntax error.
    if (hadError)
      return;
    System.out.println(intepreter.interpret(expression));
  }

  public static void error(int line, String message) {
    report(line, "", message);
  }

  public static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }

  private static void report(int line, String where, String message) {
    System.err.printf("[line %s] Error %s: %s\n", line, where, message);
  }

  public static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() + "\n[line " + error.getToken().line + "]");
    hadRuntimeError = true;
  }
}
