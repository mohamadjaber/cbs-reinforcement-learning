package aub.edu.lb.bip.expression;

import aub.edu.lb.bip.api.TEnumType;
import aub.edu.lb.bip.api.TogetherSyntax;

public class TVariable extends TNamedElement {
	
	protected TEnumType type; 


	public TVariable() {
	}
	
	public TVariable(String n, TEnumType t) {
		name = n; 
		type = t; 
	}
	
	public TEnumType getType() {
		return type;
	}
	
	public TAssignmentAction create() {
		return new TAssignmentAction(this, null, true);
	}
	
	public TAssignmentAction create(TExpression e) {
		return new TAssignmentAction(this, e, true);
	}
	
	public TAssignmentAction set(TExpression e) {
		return new TAssignmentAction(this, e, false);
	}
	
	
	public TVariable getInstance() {
		return new TVariable(name, type);
	}
	
	public String createType() {
		return type.getName();
	}
	
	@Override
	public String getName() {
		if(type.equals(TEnumType.WIRE_BOOLEAN) || type.equals(TEnumType.WIRE_INT)) {
			return TogetherSyntax.wire_prefix + name;
		}
		else return name; 
	}
	
	@Override
	public String toString() {
		if(type.equals(TEnumType.WIRE_BOOLEAN) || type.equals(TEnumType.WIRE_INT)) {
			return TogetherSyntax.wire_prefix + name;
		}
		else return name; 
	}
}
