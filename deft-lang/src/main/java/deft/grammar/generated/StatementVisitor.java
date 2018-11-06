package deft.grammar.generated;

public interface StatementVisitor<R> {
  R visitBlockStatement(BlockStatement statement);

  R visitExpressionStatement(ExpressionStatement statement);

  R visitPrintStatement(PrintStatement statement);

  R visitVariableStatement(VariableStatement statement);
}
