package deft.tools;

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
import deft.lang.Scanner;

public class AstPrinterTest {
	@Test
	public void testPrinter() {
		String expected = "(* (- 123) (group 45.67))";
		Expression expression = new Binary(new Unary(new Token(TokenType.MINUS, "-", null, 1), new Literal(123)),
				new Token(TokenType.STAR, "*", null, 1), new Grouping(new Literal(45.67)));
		Scanner scanner = new Scanner(expected);
		scanner.scanTokens();
		Assert.assertEquals(expected, new AstPrinter().print(expression));
	}
}
