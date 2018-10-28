package deft.lang;

import deft.grammar.generated.Binary;
import deft.grammar.generated.Expression;
import deft.grammar.generated.Grouping;
import deft.grammar.generated.Literal;
import deft.grammar.generated.Unary;
import deft.grammar.generated.Visitor;

public class AstPrinter implements Visitor<String> {
	public String print(Expression expr) {
		return expr.accept(this);
	}

	@Override
	public String visitBinary(Binary expr) {
		return parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	@Override
	public String visitGrouping(Grouping expr) {
		return parenthesize("group", expr.expression);
	}

	@Override
	public String visitLiteral(Literal expr) {
		if (expr.value == null)
			return "nil";
		return expr.value.toString();
	}

	@Override
	public String visitUnary(Unary expr) {
		return parenthesize(expr.operator.lexeme, expr.right);
	}

	private String parenthesize(String name, Expression... exprs) {
		StringBuilder builder = new StringBuilder();

		builder.append("(").append(name);
		for (Expression expr : exprs) {
			builder.append(" ");
			builder.append(expr.accept(this));
		}
		builder.append(")");

		return builder.toString();
	}
}
