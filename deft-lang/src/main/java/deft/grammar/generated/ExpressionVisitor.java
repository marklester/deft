package deft.grammar.generated;

public interface ExpressionVisitor<R> {
  R visitAssign(Assign expression);

  R visitBinary(Binary expression);

  R visitGrouping(Grouping expression);

  R visitLiteral(Literal expression);

  R visitUnary(Unary expression);

  R visitVariable(Variable expression);
}
