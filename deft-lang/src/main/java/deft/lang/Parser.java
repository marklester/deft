package deft.lang;

import static deft.grammar.TokenType.BANG;
import static deft.grammar.TokenType.BANG_EQUAL;
import static deft.grammar.TokenType.EQUAL_EQUAL;
import static deft.grammar.TokenType.FALSE;
import static deft.grammar.TokenType.GREATER;
import static deft.grammar.TokenType.GREATER_EQUAL;
import static deft.grammar.TokenType.LEFT_PAREN;
import static deft.grammar.TokenType.LESS;
import static deft.grammar.TokenType.LESS_EQUAL;
import static deft.grammar.TokenType.MINUS;
import static deft.grammar.TokenType.NIL;
import static deft.grammar.TokenType.NUMBER;
import static deft.grammar.TokenType.PLUS;
import static deft.grammar.TokenType.RIGHT_PAREN;
import static deft.grammar.TokenType.SLASH;
import static deft.grammar.TokenType.STAR;
import static deft.grammar.TokenType.STRING;
import static deft.grammar.TokenType.TRUE;
import java.util.List;
import deft.Deft;
import deft.grammar.Token;
import deft.grammar.TokenType;
import deft.grammar.generated.Binary;
import deft.grammar.generated.Expression;
import deft.grammar.generated.Grouping;
import deft.grammar.generated.Literal;
import deft.grammar.generated.Unary;
import deft.lang.errors.ParseError;

public class Parser {
  private final List<Token> tokens;
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public Expression parse() {
    try {
      return expression();
    } catch (ParseError error) {
      return null;
    }
  }

  // equality → comparison ( ( "!=" | "==" ) comparison )* ;
  private Expression expression() {
    return equality();
  }

  private Expression equality() {
    Expression expr = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expression right = comparison();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  // comparison → addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ;
  private Expression comparison() {
    Expression expr = addition();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expression right = addition();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression addition() {
    Expression expr = multiplication();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expression right = multiplication();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression multiplication() {
    Expression expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expression right = unary();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expression unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expression right = unary();
      return new Unary(operator, right);
    }

    return primary();
  }

  private Expression primary() {
    if (match(FALSE))
      return new Literal(false);
    if (match(TRUE))
      return new Literal(true);
    if (match(NIL))
      return new Literal(null);

    if (match(NUMBER, STRING)) {
      return new Literal(previous().literal);
    }

    if (match(LEFT_PAREN)) {
      Expression expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Grouping(expr);
    }
    throw error(peek(), "Expect expression.");
  }

  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();

    throw error(peek(), message);
  }

  private ParseError error(Token token, String message) {
    Deft.error(token, message);
    return new ParseError();
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == TokenType.SEMICOLON)
        return;

      switch (peek().type) {
        case CLASS:
        case FUN:
        case VAR:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
          return;
      }

      advance();
    }
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd())
      current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == TokenType.EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
}
