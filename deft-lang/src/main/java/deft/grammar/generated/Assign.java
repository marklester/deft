package deft.grammar.generated;

public class Assign implements Expression {
  public final deft.grammar.Token name;

  public final Expression value;

  public Assign(deft.grammar.Token name, Expression value) {
    this.name = name;
    this.value = value;
  }

  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitAssign(this);
  }
}
