package aub.edu.lb.bip.expression;

import aub.edu.lb.bip.api.TEnumType;

public class TArrayVariable extends TVariable {
	
	private TExpression index; 
	
	public TArrayVariable(String n, TEnumType t, TExpression index) {
		super(n,t);
		this.index = index; 
	}
	
	public TExpression getIndex() {
		return index; 
	}
	
	public String createType() {
		return type.getName();
	}
	
	public String toString() {
		return name + "[" + index + "]";
	}
}
