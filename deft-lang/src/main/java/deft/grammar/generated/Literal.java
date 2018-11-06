package deft.grammar.generated;

public class Literal implements Expression {
  public final Object value;

  public Literal(Object value) {
    this.value = value;
  }

  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitLiteral(this);
  }
}
