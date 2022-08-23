package com.language.tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		List<String> exprTypes = Arrays.asList(
				"Binary   : Expr left, Token operator, Expr right",
				"Grouping : Expr expression",
				"Literal  : Object value",
				"Unary    : Token operator, Expr right");
		
		if (args.length != 1) {
			System.err.println("Usage: generate_ast <output_directory>");
			System.exit(64);
		}
		String outputDirectory = args[0];
		
		defineAst(outputDirectory, "Expr", exprTypes);
		
	}
	
	private static void defineAst(String outputDirectory, String baseName, List<String> types)
			throws FileNotFoundException, UnsupportedEncodingException {
		String path = outputDirectory + "/" + baseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		
		writer.println("package com.language.lox;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println();
		writer.println("abstract class " + baseName + " {");
		
		defineVisitor(writer, baseName, types);
		
		types.forEach(type -> {
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim();
			
			defineType(writer, baseName, className, fields);
			
			writer.println();
		});
		writer.println("	abstract <R> R accept(Visitor<R> visitor);");
		
		writer.println("}");
		writer.close();
	}
	
	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
		writer.println("	interface Visitor<R> {");
		for (String type : types) {
			writer.println();
			String typeName = type.split(":")[0].trim();
			writer.println(
					"		R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
			
		}
		writer.println("	}");
		writer.println();
		
	}
	
	private static void defineType(PrintWriter writer, String baseName, String className, String fields) {
		String[] fieldList = fields.split(",");
		
		writer.println("	static class " + className + " extends " + baseName + " {");
		writer.println();
		
		//fields
		for (String field : fieldList) {
			writer.println("		final " + field.trim() + ";");
		}
		writer.println();
		
		//constructor
		writer.println("		" + className + "(" + fields + ") {");
		
		for (String field : fieldList) {
			String fieldName = field.trim().split(" ")[1];
			writer.println("			this." + fieldName + " = " + fieldName + ";");
		}
		writer.println("		}");
		
		writer.println();
		writer.println("		@Override");
		writer.println("		<R> R accept(Visitor<R> visitor) {");
		writer.println("      		return visitor.visit" + className + baseName + "(this);");
		writer.println("    	}");
		writer.println("	}");
	}
}
