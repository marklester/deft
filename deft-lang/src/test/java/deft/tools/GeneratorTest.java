package deft.tools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.squareup.javapoet.JavaFile;

public class GeneratorTest {
	@Test
	public void testGenerate() throws IOException {
		AstGenerator generator = new AstGenerator();
		List<JavaFile> jfiles = generator.buildAstJavaFiles(AstGenerator.EXPRESSION_TYPE_DEFINITIONS);
		Path path = Paths.get("src/main/java/");
		for (JavaFile jfile : jfiles) {
			jfile.writeTo(path.toFile());
		}
	}
}
