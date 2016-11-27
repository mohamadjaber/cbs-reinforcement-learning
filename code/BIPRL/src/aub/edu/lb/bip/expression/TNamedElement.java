package aub.edu.lb.bip.expression;

import aub.edu.lb.bip.expression.TExpression;

public class TNamedElement implements TExpression {
	protected String name; 
	
	public TNamedElement() {
		
	}
	
	public TNamedElement(String name) {
		this.name = name; 
	}
	
	public String getName() {
		return name; 
	}
	
	public String toString() {
		return name; 
	}

}
