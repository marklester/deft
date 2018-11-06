package deft.grammar.generated;

public class BlockStatement implements Statement {
  public final java.util.List<Statement> statements;

  public BlockStatement(java.util.List<Statement> statements) {
    this.statements = statements;
  }

  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitBlockStatement(this);
  }
}
