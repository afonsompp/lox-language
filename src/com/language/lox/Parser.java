package com.language.lox;

import java.util.List;

import static com.language.lox.TokenType.*;

public class Parser {
	
	private List<Token> tokens;
	private int current = 0;
	
	public Parser(List<Token> tokens) {this.tokens = tokens;}
	
	Expr parse() {
		try {
			return expression();
		} catch (ParseErrorException error) {
			return null;
		}
	}
	
	// create expression in precedence order
	private Expr expression() {
		return equality();
	}
	
	private Expr equality() {
		Expr expr = comparison();
		while (match(EQUAL_EQUAL, BANG_EQUAL)) {
			Token operator = previous();
			Expr right = comparison();
			expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}
	
	private Expr comparison() {
		Expr expr = term();
		while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = previous();
			Expr right = term();
			expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}
	
	private Expr term() {
		Expr expr = factor();
		while (match(MINUS, PLUS)) {
			Token operator = previous();
			Expr right = factor();
			expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}
	
	private Expr factor() {
		Expr expr = unary();
		while (match(SLASH, STAR)) {
			Token operator = previous();
			Expr right = unary();
			expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}
	
	private Expr unary() {
		if (match(BANG, MINUS)) {
			Token operator = previous();
			Expr right = term();
			return new Expr.Unary(operator, right);
		}
		return primary();
	}
	
	private Expr primary() {
		if (match(TRUE)) {
			return new Expr.Literal(true);
		}
		if (match(FALSE)) {
			return new Expr.Literal(false);
		}
		if (match(NIL)) {
			return new Expr.Literal(null);
		}
		if (match(NUMBER, STRING)) {
			return new Expr.Literal(previous().literal);
		}
		
		if (match(LEFT_PAREN)) {
			Expr expr = expression();
			consume(RIGHT_PAREN, "Expect ')' after expression");
			return new Expr.Grouping(expr);
		}
		throw error(peek(), "Expect expression.");
	}
	
	private Token consume(TokenType type, String message) {
		if (check(type)) return advance();
		
		throw error(peek(), message);
	}
	
	private ParseErrorException error(Token token, String message) {
		Lox.error(token, message);
		return new ParseErrorException();
	}
	
	// verify if each passed token is equal with current token
	private boolean match(TokenType... tokens) {
		for (var token : tokens) {
			if (check(token)) {
				advance();
				return true;
			}
		}
		return false;
	}
	
	// verify if passed token type is equal a current token
	private boolean check(TokenType token) {
		if (isAtEnd()) {
			return false;
		}
		
		return peek().type == token;
	}
	
	// verify if is the expression end
	private boolean isAtEnd() {
		return peek().type == EOF;
	}
	
	// get the current token
	private Token peek() {
		return tokens.get(current);
	}
	
	// get the previous token
	private Token previous() {
		return tokens.get(current - 1);
	}
	
	// get the current token and advance to next token
	private Token advance() {
		if (!isAtEnd()) {
			current++;
		}
		return previous();
	}
	
	private void synchronize() {
		advance();
		
		while (!isAtEnd()) {
			if (previous().type == SEMICOLON) return;
			
			switch (peek().type) {
				case CLASS:
				case FUN:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					return;
			}
			advance();
		}
	}
	
	static class ParseErrorException extends RuntimeException {
	}
	
}
