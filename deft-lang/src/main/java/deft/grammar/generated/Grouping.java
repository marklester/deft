package deft.grammar.generated;

public class Grouping implements Expression {
  public final Expression expression;

  public Grouping(Expression expression) {
    this.expression = expression;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitGrouping(this);
  }
}
