package deft.grammar.generated;

public class Unary implements Expression {
  public final deft.grammar.Token operator;

  public final Expression right;

  public Unary(deft.grammar.Token operator, Expression right) {
    this.operator = operator;
    this.right = right;
  }

  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitUnary(this);
  }
}
