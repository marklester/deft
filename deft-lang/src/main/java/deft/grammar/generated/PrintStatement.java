package deft.grammar.generated;

public class PrintStatement implements Statement {
  public final Expression expression;

  public PrintStatement(Expression expression) {
    this.expression = expression;
  }

  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitPrintStatement(this);
  }
}
