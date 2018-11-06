package deft.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import deft.Deft;
import deft.OutputHandler;
import deft.grammar.Token;
import deft.grammar.generated.Statement;

public class InterpreterTest {

  @Test
  public void testInterpreter() throws IOException {
    String program = Files.readString(Paths.get("src/test/resources/add.deft"));

    Scanner scanner = new Scanner(program);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Statement> statements = parser.parse();
    Interpreter intrepreter = new Interpreter(Deft.outputHandler);
    intrepreter.interpret(statements);
  }

  @Test
  public void testInitError() throws IOException {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream err = new PrintStream(baos, true, "UTF-8");
    String program = Files.readString(Paths.get("src/test/resources/var_init_error.deft"));

    Scanner scanner = new Scanner(program);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Statement> statements = parser.parse();
    Interpreter intrepreter =
        new Interpreter(new OutputHandler(System.out, err));
    intrepreter.interpret(statements);
    String errors = baos.toString(StandardCharsets.UTF_8);
    Assert.assertTrue(errors.contains("b was not initialized"));
  }
}
