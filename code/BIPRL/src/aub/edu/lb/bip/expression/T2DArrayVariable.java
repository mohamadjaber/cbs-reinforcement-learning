package aub.edu.lb.bip.expression;

import aub.edu.lb.bip.api.TEnumType;

public class T2DArrayVariable extends TVariable {
	private TExpression line;
	private TExpression column; 
	
	public T2DArrayVariable(String n, TEnumType t, TExpression line, TExpression column) {
		super(n,t);
		this.line = line; 
		this.column = column; 
	}
	
	public TExpression getNbOfLines() {
		return line;
	}
	
	public TExpression getNbOfColumns() {
		return line;
	}
	
	public String createType() {
		return type.getName() + "[][]";
	}
	
	public String toString() {
		return name + "[" + line + "][" + column + "]";
	}
}
