package deft;

import java.util.HashMap;
import java.util.Map;
import deft.grammar.Token;
import deft.lang.errors.RuntimeError;

public class Environment {
  private final Map<String, Object> values = new HashMap<>();
  final Environment enclosing;

  public Environment() {
    enclosing = null;
  }

  public Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  public void define(String name, Object value) {
    values.put(name, value);
  }

  public Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      Object val = values.get(name.lexeme);
      if (val == null) {
        throw new RuntimeError(name, name.lexeme+" was not initialized");
      }
      return val;
    }
    if (enclosing != null)
      return enclosing.get(name);

    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

  public void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }
    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }

    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }
}
