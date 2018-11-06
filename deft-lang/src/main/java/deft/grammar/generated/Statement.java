package deft.grammar.generated;

public interface Statement {
  <R> R accept(StatementVisitor<R> visitor);
}
