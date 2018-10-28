package deft.grammar.generated;

public interface Expression {
  <R> R accept(Visitor<R> visitor);
}
