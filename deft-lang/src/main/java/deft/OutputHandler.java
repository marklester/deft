package deft;

import java.io.PrintStream;
import deft.grammar.Token;
import deft.grammar.TokenType;
import deft.lang.errors.RuntimeError;

public class OutputHandler {
  private boolean hadRuntimeError = false;
  private boolean hadError = false;
  private final PrintStream stdout;
  private final PrintStream stderr;

  public OutputHandler() {
    stdout = System.out;
    stderr = System.err;
  }

  public OutputHandler(PrintStream stdout, PrintStream stderr) {
    this.stdout = stdout;
    this.stderr = stderr;
  }

  public void error(int line, String message) {
    report(line, "", message);
  }

  public void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }

  private void report(int line, String where, String message) {
    String output = String.format("[line %s] Error %s: %s\n", line, where, message);
    stderr.print(output);
  }

  public void runtimeError(RuntimeError error) {
    String errorMessage = error.getMessage() + "\n[line " + error.getToken().line + "]\n";

    stderr.print(errorMessage);
    setHadRuntimeError(true);
  }

  public boolean hadRuntimeError() {
    return isHadRuntimeError();
  }

  public void println(String stringify) {
    String out = stringify + "\n";
    stdout.print(out);
  }

  public boolean isHadError() {
    return hadError;
  }

  public void setHadError(boolean hadError) {
    this.hadError = hadError;
  }

  public boolean isHadRuntimeError() {
    return hadRuntimeError;
  }

  public void setHadRuntimeError(boolean hadRuntimeError) {
    this.hadRuntimeError = hadRuntimeError;
  }
}
