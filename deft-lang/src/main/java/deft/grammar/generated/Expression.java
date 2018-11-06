package deft.grammar.generated;

public interface Expression {
  <R> R accept(ExpressionVisitor<R> visitor);
}
