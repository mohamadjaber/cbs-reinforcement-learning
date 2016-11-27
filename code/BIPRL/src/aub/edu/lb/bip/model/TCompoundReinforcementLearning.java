package aub.edu.lb.bip.model;



import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TCompositeAction;
import aub.edu.lb.bip.expression.TDoTogetherAction;
import aub.edu.lb.bip.expression.TNamedElement;
import aub.edu.lb.bip.expression.TWhileAction;
import aub.edu.lb.bip.rl.ValueIterator;
import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;
import ujf.verimag.bip.Core.Interactions.CompoundType;

/**
 * Variables and wires creation. 
 * @do_together {
 * 		initialization
 * }
 * while(true) {
 * 		@do_together {
 * 			1. compute port local enablement
 * 			2. compute interaction enablement
 * 			3. filter interaction according to priority, if necessary. 
 * 			4. selection one interaction
 * 			5. execute the corresponding action of the selected interaction.
 * 			6. execute local transitions
 * 		}
 * }
 * 
 * In this case, we assume that if an interaction modifies a variable through its action, then the corresponding transition will not modify that variable. 
 * Note that, in most of the bip models this is the case. 
 *
 */
public class TCompoundReinforcementLearning extends TCompound {

	
	public TCompoundReinforcementLearning(String bipFile, boolean defaultInitializeVariables, String preCondition, String postCondition, String badStateFile) {
		super(bipFile, true, defaultInitializeVariables, preCondition, postCondition, badStateFile);
	}
	
	public TCompoundReinforcementLearning(String bipFile, String badStateFile) {
		super(bipFile, true, false, null, null, badStateFile);
	}
	
	public void generalInitialize() {
		initializeRL();
	}

	
	public TCompoundReinforcementLearning(String bipFile, CompoundType compound, boolean defaultInitializeVariables, String badStateFile) {
		super(bipFile, true, defaultInitializeVariables, badStateFile);
	}
	

	private void initializeRL() {
		Kripke transitionSystem = new Kripke(compound);
		valueIterator = new ValueIterator(transitionSystem, badStateFile);
	}

	@Override
	protected void mainWhileLoopAction() {
		TCompositeAction ca = new TCompositeAction();
		
		TDoTogetherAction doTogetherCycle1 = new TDoTogetherAction();	

		TCompositeAction caCycle1 = new TCompositeAction();
		doTogetherCycle1.setAction(caCycle1);
		
		ca.getContents().add(doTogetherCycle1);
		
		TWhileAction whileLoop = new TWhileAction(new TNamedElement(TogetherSyntax.true_condition));
		whileLoop.setAction(ca);
		togetherAction.getContents().add(whileLoop);
		setRandomSelector(caCycle1);
		setCurrentState(caCycle1);
		setLocalPortEnablement(caCycle1);
		setInteractionEnablement(caCycle1);
		setPortInteractionEnablement(caCycle1);
		setNextStateFunctionInteraction(caCycle1);
		setNextStateFunctionLocationVariable(caCycle1);
		injectFooter(caCycle1);
		injectPostCondition(caCycle1);	
	}
	
	@Override
	protected void setInteractionEnablement(TCompositeAction ca) {
		setFirstInteractionEnablement(ca);
		setInteractionEnablementRE(ca);
		if (withPriority) setFilterInteractionPriority(ca);
		setSelectOneInteraction(ca, withPriority);
	}
	
	private void setInteractionEnablementRE(TCompositeAction action) {
		action.getContents().add(tInteractions.getInteractionEnablementRL());
	}
	
	@Override
	protected void createInteractions() {
		togetherAction.getContents().add(this.getTInteractions().create());
		togetherAction.getContents().add(this.getTInteractions().getTInteractionsFirstEnable().create());
		togetherAction.getContents().add(this.getTInteractions().getTInteractionsRuntimeEnforcement().create());
		initializeQValueTable();
		if (withPriority)
			togetherAction.getContents().add(this.getTInteractions().getTInteractionsFilterPriority().create());
	}
	
	private void initializeQValueTable() {
		for (KripkeState state : valueIterator.transitionSystem.getStates()) {
			int stateId = valueIterator.transitionSystem.getStateId(state.getState().toString());
			for (Transition t : state.getTransitions()) {
				double value = valueIterator.qValue[stateId][t.getLabel().getId()];
				togetherAction.getContents().add(new TNamedElement(TogetherSyntax.interactions_filtered_re + "[\""
						+ state.getState().getLocalIds() + "" + t.getLabel().getId() + "\"] = " + value + ";"));
			}
		}
	}
	
}

