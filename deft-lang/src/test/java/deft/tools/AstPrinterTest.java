package deft.tools;

import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import deft.grammar.Token;
import deft.grammar.TokenType;
import deft.grammar.generated.Binary;
import deft.grammar.generated.Expression;
import deft.grammar.generated.Grouping;
import deft.grammar.generated.Literal;
import deft.grammar.generated.Unary;
import deft.lang.AstPrinter;
import deft.lang.Interpreter;
import deft.lang.Parser;
import deft.lang.Scanner;

public class AstPrinterTest {
  @Test
  public void testPrintExpression() {
    AstPrinter printer = new AstPrinter();
    String expected = "(* (- 123) (group 45.67))";
    Expression expression =
        new Binary(new Unary(new Token(TokenType.MINUS, "-", null, 1), new Literal(123)),
            new Token(TokenType.STAR, "*", null, 1), new Grouping(new Literal(45.67)));

    Assert.assertEquals(expected, printer.print(expression));
  }

  @Test
  public void testPrintParsedExpression() {
    String input = "1==3";
    String expected = "(== 1.0 3.0)";
    AstPrinter printer = new AstPrinter();

    Scanner scanner = new Scanner(input);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    Expression expr = parser.parse();
    Assert.assertEquals(expected, printer.print(expr));
  }
  
  @Test
  public void testInterpret() {
    String input = "1+3";
    String expected = "(+ 1.0 3.0)";

    AstPrinter printer = new AstPrinter();

    Scanner scanner = new Scanner(input);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    Expression expr = parser.parse();
    Assert.assertEquals(expected, printer.print(expr));
    Interpreter intrepreter = new Interpreter();
    Assert.assertEquals("4", intrepreter.interpret(expr));
  }
}
