package aub.edu.lb.bip.expression;

import aub.edu.lb.bip.api.TogetherSyntax;

public class TWhileAction implements TAction {
	private TAction action; 
	private TExpression condition; 
	
	public TWhileAction(TAction action, TExpression condition) {
		this.action = action; 
		this.condition = condition; 
	}
	
	public TWhileAction(TExpression condition) {
			this.condition = condition; 
	}
	
	public TWhileAction() { 
	}
	
	public TAction getAction() {
		return action; 
	}
	
	public TExpression getCondition() {
		return condition; 
	}
	
	public void setAction(TAction action) {
		this.action = action; 
	}
	
	public void setCondition(TExpression condition) {
		this.condition = condition; 
	}
	
	
	public String toString() {
		return TogetherSyntax.while_loop + "(" + condition + ") {\n"+
				action + "\n" +
				"}";
	}
	

}
