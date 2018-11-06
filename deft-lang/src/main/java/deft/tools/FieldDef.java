package deft.tools;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;

public class FieldDef {
  private final String type;
  private final String name;

  public FieldDef(String name, String type) {
    this.type = type;
    this.name = name;
  }

  public FieldDef(String name, String type, String paramOfType) {
    this.type = type + "<" + paramOfType + ">";
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public ParameterSpec toParameterSpec() {
    return ParameterSpec.builder(ClassName.get("", getType()), getName()).build();
  }
}
