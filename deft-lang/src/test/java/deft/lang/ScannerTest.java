package deft.lang;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import deft.grammar.Token;
import deft.grammar.TokenType;

public class ScannerTest {
	@Test
	public void testIdentifier() {
		Scanner scanner = new Scanner("var x = 3");
		List<Token> tokens = scanner.scanTokens();
		Assert.assertEquals(5, tokens.size());
		Assert.assertEquals(tokens.toString(),TokenType.VAR, tokens.get(0).type);
		Assert.assertEquals(tokens.toString(),TokenType.IDENTIFIER, tokens.get(1).type);
		Assert.assertEquals(tokens.toString(),TokenType.EQUAL, tokens.get(2).type);
		Assert.assertEquals(tokens.toString(),TokenType.NUMBER, tokens.get(3).type);
		Assert.assertEquals(tokens.toString(),TokenType.EOF, tokens.get(4).type);
	}
}
