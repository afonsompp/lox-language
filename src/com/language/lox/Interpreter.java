package com.language.lox;

import java.util.Objects;

public class Interpreter implements Expr.Visitor {
	
	void interpret(Expr expression) {
		try {
			Object value = evaluate(expression);
			System.out.println(stringify(value));
		} catch (RuntimeError error) {
			Lox.runtimeError(error);
		}
	}
	
	private String stringify(Object object) {
		if (object == null) return "nil";
		
		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}
		
		return object.toString();
	}
	
	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		Object right = evaluate(expr.right);
		Object left = evaluate(expr.left);
		
		switch (expr.operator.type) {
			case PLUS: {
				if (left instanceof Double && right instanceof Double) {
					checkNumberOperands(expr.operator, left, right);
					return (double) left + (double) right;
				}
				if (left instanceof String || right instanceof String) {
					return left + (String) right;
				}
				throw new RuntimeError(expr.operator, " both operands must be a number or a string.");
				
			}
			case MINUS: {
				checkNumberOperands(expr.operator, left, right);
				return (double) left - (double) right;
			}
			case SLASH: {
				checkNumberOperands(expr.operator, left, right);
				return (double) left / (double) right;
			}
			case STAR: {
				checkNumberOperands(expr.operator, left, right);
				return (double) left * (double) right;
			}
			case GREATER: {
				checkNumberOperands(expr.operator, left, right);
				return (double) left > (double) right;
			}
			case GREATER_EQUAL: {
				checkNumberOperands(expr.operator, left, right);
				return (double) left >= (double) right;
			}
			case LESS: {
				checkNumberOperands(expr.operator, left, right);
				return (double) left < (double) right;
			}
			case LESS_EQUAL: {
				checkNumberOperands(expr.operator, left, right);
				return (double) left <= (double) right;
			}
			case EQUAL_EQUAL: {
				return isEqual(left, right);
			}
			case BANG_EQUAL: {
				return !isEqual(left, right);
			}
			
		}
		return null;
	}
	
	@Override
	public Object visitGroupingExpr(Expr.Grouping expr) {
		return evaluate(expr.expression);
	}
	
	@Override
	public Object visitLiteralExpr(Expr.Literal expr) {
		return expr.value;
	}
	
	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
		Object right = evaluate(expr.right);
		
		switch (expr.operator.type) {
			case MINUS: {
				checkNumberOperands(expr.operator, right);
				return -(double) right;
			}
			case BANG: {
				return !isTruthy(right);
			}
		}
		return null;
	}
	
	private boolean isTruthy(Object obj) {
		if (Objects.isNull(obj)) return false;
		
		if (obj instanceof Boolean) return (boolean) obj;
		
		return true;
	}
	
	private boolean isEqual(Object a, Object b) {
		if (a == null && b == null) return true;
		if (a == null) return false;
		
		return a.equals(b);
	}
	
	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}
	
	private void checkNumberOperands(Token operator, Object... operands) {
		for (Object operand : operands) {
			if (operand instanceof Double) continue;
			throw new RuntimeError(operator, "Operand must be a number.");
		}
	}
	
}
