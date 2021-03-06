package aub.edu.lb.bip.expression;

public class TAssignmentAction implements TAction {
    private TVariable assignedTarget;
    private TExpression assignedValue; 
    private boolean create;


        
    public TAssignmentAction(TVariable at, TExpression av, boolean create) {
		assignedValue = av; 
		assignedTarget = at; 
		this.create = create; 	
    }
	        
	public boolean getCreate() {
		return create; 
	}
	
	
	public TNamedElement getAssignedTarget() {
		return assignedTarget;
	}
	
	public TExpression getAssignedValue() {
		return assignedValue;
	}
	
	public String toString() {
		String s = "";
		if(create) {
			s += assignedTarget.createType() + " ";
			if(assignedTarget instanceof T2DArrayVariable) {
				T2DArrayVariable twoDV = (T2DArrayVariable) assignedTarget;
				s += assignedTarget.getName() + "[" + twoDV.getNbOfLines() + "][" + twoDV.getNbOfColumns() + "]";
			} else if(assignedTarget instanceof TArrayVariable) {
				TArrayVariable arrayVariable = (TArrayVariable) assignedTarget;
				s += assignedTarget.getName() + "[" + arrayVariable.getIndex() + "]";
				
			} else s += assignedTarget.getName();
		}
		else
			s += assignedTarget;
		if(assignedValue == null)
			s += ";";
		else {
			s += " = " + assignedValue + ";";
		}
		return s;
	}
}