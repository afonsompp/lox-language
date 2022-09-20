package com.language.lox;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Lox {
	
	private static final Interpreter interpreter = new Interpreter();
	private static boolean hadError = false;
	private static boolean hadRuntimeError = false;
	
	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: jlox [script]");
			System.exit(64);
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}
	}
	
	private static void runPrompt() {
		Scanner scanner = new Scanner(System.in);
		for (; ; ) {
			System.out.print(">> ");
			String line = scanner.nextLine();
			if (line != null) {
				if (line.equals("exit")) {
					break;
				} else {
					run(line);
					hadError = false;
				}
			}
		}
	}
	
	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
		if (hadError) System.exit(65);
		if (hadRuntimeError) System.exit(70);
		
	}
	
	private static void run(String source) {
		LoxScanner scanner = new LoxScanner(source);
		List<Token> tokens = scanner.scanTokens();
		Parser parser = new Parser(tokens);
		Expr expressions = parser.parse();
		
		if (hadError) System.exit(65);
		interpreter.interpret(expressions);
		// for (Token token : tokens) {
		// 	System.out.println(token);
		// }
	}
	
	static void error(int line, String message) {
		report(line, "", message);
	}
	
	private static void report(int line, String where, String message) {
		System.err.println("[line" + line + "]" + where + " Error" + "" + ": " + message);
		hadError = true;
	}
	
	static void error(Token token, String message) {
		if (token.type == TokenType.EOF) {
			report(token.line, " at end", message);
		} else {
			report(token.line, " at '" + token.lexeme + "'", message);
		}
	}
	
	static void runtimeError(RuntimeError error) {
		System.err.println(error.getMessage() +
						   "\n[line " + error.token.line + "]");
		hadRuntimeError = true;
	}
	
}
