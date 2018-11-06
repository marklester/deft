package deft.tools;

import java.util.Arrays;
import java.util.List;
import deft.grammar.Token;

public class AstConstants {
  public final static String PACKAGE_NAME = "deft.grammar.generated";
  public final static String EXPRESSION_INTERFACE_NAME = "Expression";
  public final static String STATEMENT_INTERFACE_NAME = "Statement";

  public static List<TypeDef> EXPRESSION_TYPE_DEFINITIONS =
      Arrays.asList(type("Assign", field(Token.class, "name"), field("Expression", "value")),
          type("Binary", field("Expression", "left"), field(Token.class, "operator"),
              field("Expression", "right")),
          type("Grouping", field("Expression", "expression")),
          type("Literal", field("Object", "value")),
          type("Unary", field(Token.class, "operator"), field("Expression", "right")),
          type("Variable", field(Token.class, "name")));

  public static List<TypeDef> STATEMENT_TYPE_DEFINITIONS = Arrays.asList(
      type("BlockStatement", field(List.class,"Statement", "statements")),
      type("ExpressionStatement", field("Expression", "expression")),
      type("PrintStatement", field("Expression", "expression")),
      type("VariableStatement", field(Token.class, "name"), field("Expression", "initializer")));

  public static FieldDef field(String type, String name) {
    return new FieldDef(name, type);
  }

  public static FieldDef field(Class<?> type, String name) {
    return new FieldDef(name, type.getName());
  }
  public static FieldDef field(Class<?> type, String parameter, String name) {
    return new FieldDef(name, type.getName(),parameter);
  }

  public static TypeDef type(String name, FieldDef... fields) {
    return new TypeDef(name, Arrays.asList(fields));
  }
}
