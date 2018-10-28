package deft.lang.errors;

import deft.grammar.Token;

public class RuntimeError extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final Token token;

  public RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
  }

  public Token getToken() {
    return token;
  }
}
