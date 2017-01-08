package aub.edu.lb.bip.model;



import aub.edu.lb.bip.api.TEnumType;
import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TCompositeAction;
import aub.edu.lb.bip.expression.TDoTogetherAction;
import aub.edu.lb.bip.expression.TNamedElement;
import aub.edu.lb.bip.expression.TVariable;
import aub.edu.lb.bip.expression.TWhileAction;
import aub.edu.lb.bip.rl.DefaultSettings;
import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;

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
public abstract class TCompoundReinforcementLearning extends TCompound {

	public Kripke transitionSystem;
	
	// default values - configuration
	protected double gamma = DefaultSettings.gamma;
	protected double badReward = DefaultSettings.badReward;
	protected double goodReward = DefaultSettings.goodReward;
	protected int initialUtility = DefaultSettings.initialUtility; 
	protected int maxIteration = DefaultSettings.DefaultMaxIteration; 

	public TCompoundReinforcementLearning(String bipFile, String badStateFile) {
		super(bipFile, true, false, badStateFile);
		transitionSystem = new Kripke(compound);
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
	
	public void setCurrentState(TCompositeAction caCycle1) {
		TVariable variable = new TVariable(TogetherSyntax.current_state_identifier, TEnumType.STRING);
		variable.create();
		String currentState = "";
		for(int i = 0; i < tComponents.size() - 1; i++) {
			currentState += "to_string(" + tComponents.get(i).getCurrentState().getName() + ") + \"_\" + ";
		}
		currentState += "to_string(" + tComponents.get(tComponents.size() - 1).getCurrentState().getName() + ")";
		caCycle1.getContents().add(variable.create(new TNamedElement(currentState)));
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
		for (KripkeState state : transitionSystem.getStates()) {
			int stateId = transitionSystem.getStateId(state.getState().toString());
			for (Transition t : state.getTransitions()) {
				double value = getQValue(stateId, t.getLabel().getId());
				togetherAction.getContents().add(new TNamedElement(TogetherSyntax.interactions_filtered_re + "[\""
						+ state.getState().getLocalIds() + "" + t.getLabel().getId() + "\"] = " + value + ";"));
			}
		}
	}
	
	public abstract double getQValue(int i, int j);
	
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
	
	public void setBadReward(double badReward) {
		this.badReward = badReward; 
	}
	public void setGoodReward(double goodReward) {
		this.goodReward = goodReward;
	}
	
	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;  
	}
}

