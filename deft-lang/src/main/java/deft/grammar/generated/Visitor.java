package deft.grammar.generated;

public interface Visitor<R> {
  R visitBinary(Binary expression);

  R visitGrouping(Grouping expression);

  R visitLiteral(Literal expression);

  R visitUnary(Unary expression);
}
