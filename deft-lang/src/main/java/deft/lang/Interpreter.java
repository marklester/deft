package deft.lang;

import deft.Deft;
import deft.grammar.Token;
import deft.grammar.generated.Binary;
import deft.grammar.generated.Expression;
import deft.grammar.generated.Grouping;
import deft.grammar.generated.Literal;
import deft.grammar.generated.Unary;
import deft.grammar.generated.Visitor;
import deft.lang.errors.RuntimeError;

public class Interpreter implements Visitor<Object> {

  public String interpret(Expression expression) {
    try {
      Object value = evaluate(expression);
      return stringify(value);
    } catch (RuntimeError error) {
      Deft.runtimeError(error);
    }
    return null;
  }

  @Override
  public Object visitBinary(Binary expression) {
    Object right = evaluate(expression.right);
    Object left = evaluate(expression.left);
    switch (expression.operator.type) {
      case GREATER:
        checkNumberOperands(expression.operator, left, right);
        return (double) left > (double) right;
      case GREATER_EQUAL:
        checkNumberOperands(expression.operator, left, right);
        return (double) left >= (double) right;
      case LESS:
        checkNumberOperands(expression.operator, left, right);
        return (double) left < (double) right;
      case LESS_EQUAL:
        checkNumberOperands(expression.operator, left, right);
        return (double) left <= (double) right;
      case MINUS:
        checkNumberOperands(expression.operator, left, right);
        return (double) left - (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }

        if (left instanceof String && right instanceof String) {
          return (String) left + (String) right;
        }
      case SLASH:
        checkNumberOperands(expression.operator, left, right);
        return (double) left / (double) right;
      case STAR:
        checkNumberOperands(expression.operator, left, right);
        return (double) left * (double) right;
      case BANG_EQUAL:
        return !isEqual(left, right);
      case EQUAL_EQUAL:
        return isEqual(left, right);
      default:
        break;
    }
    return null;
  }

  private boolean isEqual(Object left, Object right) {
    // nil is only equal to nil.
    if (left == null && right == null)
      return true;
    if (left == null)
      return false;

    return left.equals(right);
  }

  @Override
  public Object visitGrouping(Grouping expression) {
    return evaluate(expression.expression);
  }

  @Override
  public Object visitLiteral(Literal expression) {
    return expression.value;
  }

  @Override
  public Object visitUnary(Unary expression) {
    Object right = evaluate(expression.right);
    checkNumberOperand(expression.operator, right);
    switch (expression.operator.type) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        return -(double) right;
      default:
        break;
    }
    return null;
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double)
      return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double)
      return;

    throw new RuntimeError(operator, "Operands must be numbers.");
  }

  private boolean isTruthy(Object value) {
    if (value == null)
      return false;
    if (value instanceof Boolean)
      return (boolean) value;
    return true;
  }

  private Object evaluate(Expression expr) {
    return expr.accept(this);
  }

  private String stringify(Object object) {
    if (object == null)
      return "nil";

    // Hack. Work around Java adding ".0" to integer-valued doubles.
    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }
}
