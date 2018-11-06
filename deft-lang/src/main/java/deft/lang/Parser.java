package deft.lang;

import static deft.grammar.TokenType.BANG;
import static deft.grammar.TokenType.BANG_EQUAL;
import static deft.grammar.TokenType.EQUAL;
import static deft.grammar.TokenType.EQUAL_EQUAL;
import static deft.grammar.TokenType.FALSE;
import static deft.grammar.TokenType.GREATER;
import static deft.grammar.TokenType.GREATER_EQUAL;
import static deft.grammar.TokenType.IDENTIFIER;
import static deft.grammar.TokenType.LEFT_PAREN;
import static deft.grammar.TokenType.LESS;
import static deft.grammar.TokenType.LESS_EQUAL;
import static deft.grammar.TokenType.MINUS;
import static deft.grammar.TokenType.NIL;
import static deft.grammar.TokenType.NUMBER;
import static deft.grammar.TokenType.PLUS;
import static deft.grammar.TokenType.RIGHT_PAREN;
import static deft.grammar.TokenType.SEMICOLON;
import static deft.grammar.TokenType.SLASH;
import static deft.grammar.TokenType.STAR;
import static deft.grammar.TokenType.STRING;
import static deft.grammar.TokenType.TRUE;
import java.util.ArrayList;
import java.util.List;
import deft.Deft;
import deft.OutputHandler;
import deft.grammar.Token;
import deft.grammar.TokenType;
import deft.grammar.generated.Assign;
import deft.grammar.generated.Binary;
import deft.grammar.generated.BlockStatement;
import deft.grammar.generated.Expression;
import deft.grammar.generated.ExpressionStatement;
import deft.grammar.generated.Grouping;
import deft.grammar.generated.Literal;
import deft.grammar.generated.PrintStatement;
import deft.grammar.generated.Statement;
import deft.grammar.generated.Unary;
import deft.grammar.generated.Variable;
import deft.grammar.generated.VariableStatement;
import deft.lang.errors.ParseError;

public class Parser {
  private final List<Token> tokens;
  private int current = 0;
  private OutputHandler outputHandler = Deft.outputHandler;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public List<Statement> parse() {
    List<Statement> statements = new ArrayList<>();
    while (!end()) {
      statements.add(declaration());
    }

    return statements;
  }

  private Statement declaration() {
    try {
      if (match(TokenType.VAR))
        return varDeclaration();

      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Statement varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expression initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new VariableStatement(name, initializer);
  }

  private Statement statement() {
    if (match(TokenType.PRINT))
      return printStatement();
    if (match(TokenType.LEFT_BRACE))
      return new BlockStatement(block());
    return expressionStatement();
  }

  private Statement expressionStatement() {
    Expression expr = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after expression.");
    return new ExpressionStatement(expr);
  }

  private Statement printStatement() {
    Expression value = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after value.");
    return new PrintStatement(value);
  }

  // equality → comparison ( ( "!=" | "==" ) comparison )* ;
  private Expression expression() {
    return assignment();
  }

  private Expression assignment() {
    Expression expr = equality();

    if (match(EQUAL)) {
      Token equals = previous();
      Expression value = assignment();

      if (expr instanceof Variable) {
        Token name = ((Variable) expr).name;
        return new Assign(name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
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

    if (match(IDENTIFIER)) {
      return new Variable(previous());
    }

    if (match(LEFT_PAREN)) {
      Expression expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Grouping(expr);
    }
    throw error(peek(), "Expect expression.");
  }

  private List<Statement> block() {
    List<Statement> statements = new ArrayList<>();

    while (!check(TokenType.RIGHT_BRACE) && !end()) {
      statements.add(declaration());
    }

    consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
    return statements;
  }

  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();

    throw error(peek(), message);
  }

  private ParseError error(Token token, String message) {
    outputHandler.error(token, message);
    return new ParseError();
  }

  private void synchronize() {
    advance();

    while (!end()) {
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
        default:
          break;
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
    if (end())
      return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!end())
      current++;
    return previous();
  }

  private boolean end() {
    return peek().type == TokenType.EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }


}
