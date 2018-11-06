package deft.grammar.generated;

public class ExpressionStatement implements Statement {
  public final Expression expression;

  public ExpressionStatement(Expression expression) {
    this.expression = expression;
  }

  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitExpressionStatement(this);
  }
}
