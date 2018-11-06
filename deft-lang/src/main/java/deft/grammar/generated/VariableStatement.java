package deft.grammar.generated;

public class VariableStatement implements Statement {
  public final deft.grammar.Token name;

  public final Expression initializer;

  public VariableStatement(deft.grammar.Token name, Expression initializer) {
    this.name = name;
    this.initializer = initializer;
  }

  public <R> R accept(StatementVisitor<R> visitor) {
    return visitor.visitVariableStatement(this);
  }
}
