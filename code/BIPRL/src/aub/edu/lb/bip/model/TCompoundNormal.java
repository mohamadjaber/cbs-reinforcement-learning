package aub.edu.lb.bip.model;



import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TCompositeAction;
import aub.edu.lb.bip.expression.TDoTogetherAction;
import aub.edu.lb.bip.expression.TNamedElement;
import aub.edu.lb.bip.expression.TWhileAction;
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
public class TCompoundNormal extends TCompound {

	public TCompoundNormal(String bipFile, boolean defaultInitializeVariables, String preCondition, String postCondition) {
		super(bipFile, true, defaultInitializeVariables, preCondition, postCondition, null, false);
	}
	
	public TCompoundNormal(String bipFile, CompoundType compound, boolean defaultInitializeVariables) {
		super(bipFile, true, defaultInitializeVariables, null);
	}
	
	public TCompoundNormal(String bipFile) {
		super(bipFile, true, false, null);
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
		setLocalPortEnablement(caCycle1);
		setInteractionEnablement(caCycle1);
		setPortInteractionEnablement(caCycle1);
		setNextStateFunctionInteraction(caCycle1);
		setNextStateFunctionLocationVariable(caCycle1);
		injectFooter(caCycle1);
		injectPostCondition(caCycle1);
	}

}
