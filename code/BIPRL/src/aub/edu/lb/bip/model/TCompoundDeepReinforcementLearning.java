package aub.edu.lb.bip.model;



import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TCompositeAction;
import aub.edu.lb.bip.expression.TDoTogetherAction;
import aub.edu.lb.bip.expression.TNamedElement;
import aub.edu.lb.bip.expression.TWhileAction;
import aub.edu.lb.bip.rl.DeepReinforcementLearning;
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
public class TCompoundDeepReinforcementLearning extends TCompound {

	DeepReinforcementLearning deepRL;
	
	public TCompoundDeepReinforcementLearning(String bipFile, boolean defaultInitializeVariables, String preCondition, String postCondition, String badStateFile,
			boolean fair) {
		super(bipFile, true, defaultInitializeVariables, preCondition, postCondition, badStateFile, fair);
	}
	
	public TCompoundDeepReinforcementLearning(String bipFile, String badStateFile, boolean fair) {
		super(bipFile, true, false, null, null, badStateFile, fair);
	}
	
	public void generalInitialize() {
		initializeRL();
	}

	
	public TCompoundDeepReinforcementLearning(String bipFile, CompoundType compound, boolean defaultInitializeVariables, String badStateFile) {
		super(bipFile, true, defaultInitializeVariables, badStateFile);
	}
	

	private void initializeRL() {
		deepRL = new DeepReinforcementLearning(compound, badStateFile);
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
	
	public void setCurrentState(TCompositeAction caCycle1) {
		TNamedElement variableInput = new TNamedElement("double * " + TogetherSyntax.current_state_identifier +
				" = new double[" + deepRL.getInputNetworkSize() + "];");
		TCompositeAction ca = new TCompositeAction();
		for(int i = 0; i < tComponents.size(); i++) {
			ca.addAction(new TNamedElement(
					TogetherSyntax.current_state_identifier + "[" + i + "] = " +
							tComponents.get(i).getCurrentState().getName() + ";"	
					));
		}
		ca.addAction(new TNamedElement(
				TogetherSyntax.current_state_identifier + "[" + tComponents.size() + "] = " +
						deepRL.getBias()[0] + ";"	
				));
		
		
		TNamedElement variableOutput = new TNamedElement("double * " + TogetherSyntax.interactions_filtered_re + " = " 
				+ TogetherSyntax.forwardProp + "(" + TogetherSyntax.current_state_identifier + ", " + deepRL.getInputNetworkSize()
				+ ");");
		
		caCycle1.getContents().add(variableInput);
		caCycle1.getContents().add(ca);
		caCycle1.getContents().add(variableOutput);
	}
	
	@Override
	protected void setInteractionEnablement(TCompositeAction ca) {
		setFirstInteractionEnablement(ca);
		System.out.println(fair);
		if(!fair) setInteractionEnablementRE(ca);
		else {
			setInteractionEnablementFairRE(ca);
		}
		if (withPriority) setFilterInteractionPriority(ca);
		setSelectOneInteraction(ca, withPriority);
	}
	
	

	
	private void setInteractionEnablementRE(TCompositeAction action) {
		action.getContents().add(tInteractions.getInteractionEnablementDeepRL());
	}
	
	private void setInteractionEnablementFairRE(TCompositeAction action) {
		action.getContents().add(tInteractions.getInteractionEnablementFairDeepRL());
	}
	
	@Override
	protected void createInteractions() {
		togetherAction.getContents().add(this.getTInteractions().create());
		togetherAction.getContents().add(this.getTInteractions().getTInteractionsFirstEnable().create());
		if (withPriority)
			togetherAction.getContents().add(this.getTInteractions().getTInteractionsFilterPriority().create());
	}
	

	
}

