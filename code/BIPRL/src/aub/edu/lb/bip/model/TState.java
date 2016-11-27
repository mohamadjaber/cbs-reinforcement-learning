package aub.edu.lb.bip.model;


import aub.edu.lb.bip.api.TransformationFunction;
import aub.edu.lb.bip.api.TEnumType;
import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TAction;
import aub.edu.lb.bip.expression.TAssignmentAction;
import aub.edu.lb.bip.expression.TBinaryExpression;
import aub.edu.lb.bip.expression.TExpression;
import aub.edu.lb.bip.expression.TIfAction;
import aub.edu.lb.bip.expression.TNamedElement;
import aub.edu.lb.bip.expression.TVariable;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryOperator;
import ujf.verimag.bip.Core.Behaviors.State;
import ujf.verimag.bip.Core.Behaviors.Transition;

public class TState extends TVariable {
	
	private State state; 
	private TComponent tComponent;
	final int value; 
	
	public TState(State s, TComponent comp) {
		state = s; 
		tComponent = comp;
		value = tComponent.stateId++;
		setName();
		type = TEnumType.CONST_INT;
	}
	
	public TAssignmentAction create() {
		return super.create(new TNamedElement("" + value));
	}

	
	
	public TIfAction nextStateFunctionTransition(Transition t) {
		TIfAction transAction = null;
		if(t.getDestination().size() == 1) { 
			TState next = tComponent.getTState(t.getDestination().get(0));
			TPort port = tComponent.getTPort(TransformationFunction.getPort(t.getTrigger()));
			transAction = new TIfAction();
			
			TExpression checkCurrentState = new TBinaryExpression(
					BinaryOperator.EQUALITY,
					tComponent.getCurrentState(), 
					new TNamedElement("" + this)
				);
	
			transAction.setCondition(new TBinaryExpression(
					BinaryOperator.LOGICAL_AND,
					checkCurrentState, 
					port.getEnable()
				));
		
			TAction updateStateAction = new TAssignmentAction(
					tComponent.getCurrentState(), 
					new TNamedElement("" + next), false
				);
			transAction.setIfCase(updateStateAction);
		}
		return transAction;
	}

	private void setName() {
		name = TogetherSyntax.state + "_" + 
				tComponent.getName() + "_" +
				state.getName();
	}
	

	public int getValue() {
		return value; 
	}
	
	public State getState() {
		return state;
	}
	
}
