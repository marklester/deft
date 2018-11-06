package deft.tools;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public class AstGenerator {

  private ClassName baseInterface;
  private ClassName visitorName;

  public AstGenerator(String packageName, String baseInterfaceName) {
    this.baseInterface = ClassName.get(packageName, baseInterfaceName);
    this.visitorName = ClassName.get(packageName, baseInterfaceName + "Visitor");
  }

  public List<JavaFile> buildAstJavaFiles(List<TypeDef> types) throws IOException {
    // The AST classes.
    List<TypeSpec> specs =
        types.stream().map(t -> defineExpressionType(t)).collect(Collectors.toList());
    TypeSpec base = defineBaseExpression();
    specs.add(defineVisitor(specs));
    specs.add(base);
    return specs.stream().map(spec -> JavaFile.builder(baseInterface.packageName(), spec).build())
        .collect(Collectors.toList());
  }

  private TypeSpec defineExpressionType(TypeDef typeDef) {
    var classBuilder = TypeSpec.classBuilder(typeDef.getName()).addSuperinterface(baseInterface)
        .addModifiers(Modifier.PUBLIC);

    Builder constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
    for (FieldDef field : typeDef.getFields()) {
      ParameterSpec fieldSpec = field.toParameterSpec();
      classBuilder.addField(fieldSpec.type, fieldSpec.name, Modifier.FINAL, Modifier.PUBLIC);
      constructor.addParameter(fieldSpec);
      constructor.addStatement("this.$N = $N", field.getName(), field.getName());
    }
    classBuilder.addMethod(constructor.build());

    TypeVariableName varType = TypeVariableName.get("R");
    ParameterizedTypeName visParamName = ParameterizedTypeName.get(visitorName, varType);

    ParameterSpec visitorInterface = ParameterSpec.builder(visParamName, "visitor").build();
    MethodSpec visMethod = MethodSpec.methodBuilder("accept").returns(varType)
        .addTypeVariable(varType).addParameter(visitorInterface).addModifiers(Modifier.PUBLIC)
        .addStatement("return visitor.visit$N(this)", typeDef.getName()).build();
    classBuilder.addMethod(visMethod);

    return classBuilder.build();
  }


  TypeSpec defineBaseExpression() {
    TypeVariableName varType = TypeVariableName.get("R");
    ParameterizedTypeName visParamName = ParameterizedTypeName.get(visitorName, varType);
    ParameterSpec visitorInterface = ParameterSpec.builder(visParamName, "visitor").build();
    MethodSpec visMethod = MethodSpec.methodBuilder("accept").returns(varType)
        .addTypeVariable(varType).addParameter(visitorInterface)
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build();
    return TypeSpec.interfaceBuilder(baseInterface).addModifiers(Modifier.PUBLIC)
        .addMethod(visMethod).build();
  }



  TypeSpec defineVisitor(List<TypeSpec> expressions) {
    TypeVariableName varType = TypeVariableName.get("R");
    var visitorInterface = TypeSpec.interfaceBuilder(visitorName).addTypeVariable(varType)
        .addModifiers(Modifier.PUBLIC);
    for (TypeSpec type : expressions) {
      ParameterSpec param =
          ParameterSpec.builder(ClassName.get("", type.name), baseInterface.simpleName().toLowerCase()).build();
      MethodSpec visitMethod = MethodSpec.methodBuilder("visit" + type.name).returns(varType)
          .addParameter(param).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build();
      visitorInterface.addMethod(visitMethod);
    }

    return visitorInterface.build();
  }
}
