package aub.edu.lb.bip.model;

import aub.edu.lb.bip.api.TEnumType;
import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TVariable;
import ujf.verimag.bip.Core.Interactions.Component;

public class TCurrentState extends TVariable {
	
	private Component component; 
	
	public TCurrentState(Component comp) {
		component = comp; 
		name = comp.getName() + "_" + TogetherSyntax.currentState;
		type = TEnumType.INT;
	}
	

	public Component getComponent() {
		return component; 
	}
	

}
