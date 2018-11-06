package deft.grammar.generated;

public class Binary implements Expression {
  public final Expression left;

  public final deft.grammar.Token operator;

  public final Expression right;

  public Binary(Expression left, deft.grammar.Token operator, Expression right) {
    this.left = left;
    this.operator = operator;
    this.right = right;
  }

  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitBinary(this);
  }
}
