package deft.tools;

import java.io.IOException;
import java.util.Arrays;
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

import deft.grammar.Token;

public class AstGenerator {
	public static ClassName BASE_INTERFACE = ClassName.get(TypeDef.PACKAGE_NAME, TypeDef.SUPER_TYPE);

	public static List<TypeDef> EXPRESSION_TYPE_DEFINITIONS = Arrays.asList(
			type("Binary", field("Expression", "left"), field(Token.class, "operator"), field("Expression", "right")),
			type("Grouping", field("Expression", "expression")), type("Literal", field("Object", "value")),
			type("Unary", field(Token.class, "operator"), field("Expression", "right")));

	public static FieldDef field(String type, String name) {
		return new FieldDef(name, type);
	}

	public static FieldDef field(Class<?> type, String name) {
		return new FieldDef(name, type.getName());
	}

	public static TypeDef type(String name, FieldDef... fields) {
		return new TypeDef(name, Arrays.asList(fields));
	}

	public List<JavaFile> buildAstJavaFiles(List<TypeDef> types) throws IOException {
		// The AST classes.
		List<TypeSpec> specs = types.stream().map(t -> defineExpressionType(t)).collect(Collectors.toList());
		TypeSpec base = defineBaseExpression();
		specs.add(defineVisitor(specs));
		specs.add(base);
		return specs.stream().map(spec -> JavaFile.builder(TypeDef.PACKAGE_NAME, spec).build())
				.collect(Collectors.toList());
	}

	private static TypeSpec defineExpressionType(TypeDef typeDef) {
		var classBuilder = TypeSpec.classBuilder(typeDef.getName()).addSuperinterface(BASE_INTERFACE)
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
		ClassName baseName = ClassName.get(TypeDef.PACKAGE_NAME, "Visitor");
		ParameterizedTypeName visParamName = ParameterizedTypeName.get(baseName, varType);

		ParameterSpec visitorInterface = ParameterSpec.builder(visParamName, "visitor").build();
		MethodSpec visMethod = MethodSpec.methodBuilder("accept").returns(varType).addTypeVariable(varType)
				.addParameter(visitorInterface).addModifiers(Modifier.PUBLIC)
				.addStatement("return visitor.visit$N(this)", typeDef.getName()).build();
		classBuilder.addMethod(visMethod);

		return classBuilder.build();
	}

	TypeSpec defineBaseExpression() {
		TypeVariableName varType = TypeVariableName.get("R");
		ClassName baseName = ClassName.get(TypeDef.PACKAGE_NAME, "Visitor");
		ParameterizedTypeName visParamName = ParameterizedTypeName.get(baseName, varType);
		ParameterSpec visitorInterface = ParameterSpec.builder(visParamName, "visitor").build();
		MethodSpec visMethod = MethodSpec.methodBuilder("accept").returns(varType).addTypeVariable(varType)
				.addParameter(visitorInterface).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build();
		return TypeSpec.interfaceBuilder(BASE_INTERFACE).addModifiers(Modifier.PUBLIC).addMethod(visMethod).build();
	}

	TypeSpec defineVisitor(List<TypeSpec> expressions) {
		ClassName baseName = ClassName.get(TypeDef.PACKAGE_NAME, "Visitor");
		TypeVariableName varType = TypeVariableName.get("R");
		var visitorInterface = TypeSpec.interfaceBuilder(baseName).addTypeVariable(varType)
				.addModifiers(Modifier.PUBLIC);
		for (TypeSpec type : expressions) {
			ParameterSpec param = ParameterSpec.builder(ClassName.get("", type.name), "expression").build();
			MethodSpec visitMethod = MethodSpec.methodBuilder("visit" + type.name).returns(varType).addParameter(param)
					.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build();
			visitorInterface.addMethod(visitMethod);
		}

		return visitorInterface.build();
	}
}
