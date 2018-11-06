package deft.grammar.generated;

public class Variable implements Expression {
  public final deft.grammar.Token name;

  public Variable(deft.grammar.Token name) {
    this.name = name;
  }

  public <R> R accept(ExpressionVisitor<R> visitor) {
    return visitor.visitVariable(this);
  }
}
