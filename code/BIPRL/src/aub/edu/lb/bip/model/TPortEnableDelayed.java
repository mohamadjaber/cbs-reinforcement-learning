package aub.edu.lb.bip.model;

import aub.edu.lb.bip.api.TEnumType;
import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TAssignmentAction;
import aub.edu.lb.bip.expression.TNamedElement;
import aub.edu.lb.bip.expression.TVariable;

@Deprecated
public class TPortEnableDelayed extends TVariable {

	private TPort tPort; 
	
	public TPortEnableDelayed(String n, TPort p) {
		name = n; 
		tPort = p; 
		type = TEnumType.BOOLEAN;
	}
	
	public TPort getPort() {
		return tPort; 
	}
	
	public TAssignmentAction initialize() {
		return super.set(new TNamedElement(TogetherSyntax.false_condition));
	}
}
