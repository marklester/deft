package deft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import deft.grammar.Token;
import deft.grammar.generated.Statement;
import deft.lang.Interpreter;
import deft.lang.Parser;
import deft.lang.Scanner;

public class Deft {
  public static final OutputHandler outputHandler = new OutputHandler();
  private static final Interpreter intepreter = new Interpreter(outputHandler);

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
      outputHandler.setHadError(false);
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, StandardCharsets.UTF_8));
    if (outputHandler.isHadError())
      System.exit(65);
    if (outputHandler.hadRuntimeError()) System.exit(70);
  }

  private static void run(String string) {
    Scanner scanner = new Scanner(string);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Statement> statements = parser.parse();

    // Stop if there was a syntax error.
    if (outputHandler.isHadError())
      return;
    intepreter.interpret(statements);
  }
}
