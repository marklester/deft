package deft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import deft.grammar.Token;
import deft.lang.Scanner;
public class Deft {
	static boolean hadError = false;
	

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: deft [script]");
			System.exit(64);
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}
	}

	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		for (;;) {
			System.out.println("> ");
			run(reader.readLine());
			hadError = false;
		}
	}

	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, StandardCharsets.UTF_8));
		if (hadError)
			System.exit(65);
	}

	private static void run(String string) {
		Scanner scanner = new Scanner(string);
		List<Token> tokens = scanner.scanTokens();
		for (Token token : tokens) {
			System.out.println(token);
		}
	}

	public static void error(int line, String message) {
		report(line, "", message);
	}

	private static void report(int line, String where, String message) {
		System.err.printf("[line %s] Error %s: %s\n", line, where, message);
	}
}
