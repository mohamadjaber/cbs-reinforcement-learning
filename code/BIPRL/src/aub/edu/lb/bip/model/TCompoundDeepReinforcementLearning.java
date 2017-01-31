package aub.edu.lb.bip.model;

import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TCompositeAction;
import aub.edu.lb.bip.expression.TDoTogetherAction;
import aub.edu.lb.bip.expression.TNamedElement;
import aub.edu.lb.bip.expression.TWhileAction;

/**
 * Variables and wires creation.
 * 
 * @do_together { initialization } while(true) {
 * @do_together { 1. compute port local enablement 2. compute interaction
 *              enablement 3. filter interaction according to priority, if
 *              necessary. 4. selection one interaction 5. execute the
 *              corresponding action of the selected interaction. 6. execute
 *              local transitions } }
 * 
 *              In this case, we assume that if an interaction modifies a
 *              variable through its action, then the corresponding transition
 *              will not modify that variable. Note that, in most of the bip
 *              models this is the case.
 *
 */
public abstract class TCompoundDeepReinforcementLearning extends TCompound {

	// configuration
	protected int capacityReplay;
	protected int numberEpisodes;
	protected double probabilityRandom;
	protected double numberResetHistoryTime;
	protected int sampleCapacityPercentage;
	protected int traceLengthIteration;
	protected int numberOfNeuronsHidden;
	protected double badReward;
	protected double goodReward;
	protected double gamma;
	protected int epoch;

	/**
	 * degree of fairness if distance (of Q value) between two interactions is
	 * less than fair, then equal the more it increases more fairness, but may
	 * affect correctness <= 0 => no fairness good value is to set it to the
	 * good reward value
	 */
	protected int fairnessDegreeDistance;

	public TCompoundDeepReinforcementLearning(String bipFile, String badStateFile, double goodReward, double badReward,
			int episodes, int epoch, int neuronsHidden, int capacityReplay, double probaRandomExploration,
			int minimumTraceLength, int sampleCapacityPercentage, int resetHistoryPeriod, double gamma, boolean debug,
			int fairnessDegreeDistance) {
		super(bipFile, true, false, badStateFile);
		this.goodReward = goodReward;
		this.badReward = badReward;
		this.traceLengthIteration = minimumTraceLength;
		this.capacityReplay = capacityReplay;
		this.epoch = epoch;
		this.gamma = gamma;
		this.numberOfNeuronsHidden = neuronsHidden;
		this.probabilityRandom = probaRandomExploration;
		this.numberResetHistoryTime = resetHistoryPeriod;
		this.numberEpisodes = episodes;
		this.sampleCapacityPercentage = sampleCapacityPercentage;
		this.fairnessDegreeDistance = fairnessDegreeDistance;
	}

	public TCompoundDeepReinforcementLearning(String bipFile, String badStateFile) {
		super(bipFile, true, false, badStateFile);
		this.fairnessDegreeDistance = -1;
	}

	public TCompoundDeepReinforcementLearning(String bipFile, String badStateFile, int fairnessDegreeDistance) {
		super(bipFile, true, false, badStateFile);
		this.fairnessDegreeDistance = fairnessDegreeDistance;
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
		cleanUpVariables(caCycle1);
		injectFooter(caCycle1);
	}

	private void cleanUpVariables(TCompositeAction caCycle1) {
		caCycle1.addAction(new TNamedElement("delete[] " + TogetherSyntax.interactions_filtered_re + ";"));
	}

	public void setCurrentState(TCompositeAction caCycle1) {
		TCompositeAction ca = new TCompositeAction();
		for (int i = 0; i < tComponents.size(); i++) {
			ca.addAction(new TNamedElement(TogetherSyntax.current_state_identifier + "[" + i + "] = "
					+ tComponents.get(i).getCurrentState().getName() + ";"));
		}
		ca.addAction(new TNamedElement(
				TogetherSyntax.current_state_identifier + "[" + tComponents.size() + "] = " + getBias()[0] + ";"));

		TNamedElement variableOutput = new TNamedElement(
				"double * " + TogetherSyntax.interactions_filtered_re + " = " + TogetherSyntax.forwardProp + "("
						+ TogetherSyntax.current_state_identifier + ", " + getInputNetworkSize() + ");");

		caCycle1.getContents().add(ca);
		caCycle1.getContents().add(variableOutput);
	}

	@Override
	protected void setInteractionEnablement(TCompositeAction ca) {
		setFirstInteractionEnablement(ca);
		if (fairnessDegreeDistance <= 0)
			setInteractionEnablementRE(ca);
		else {
			setInteractionEnablementFairRE(ca);
		}
		if (withPriority)
			setFilterInteractionPriority(ca);
		setSelectOneInteraction(ca, withPriority);
	}

	private void setInteractionEnablementRE(TCompositeAction action) {
		action.getContents().add(tInteractions.getInteractionEnablementDeepRL());
	}

	private void setInteractionEnablementFairRE(TCompositeAction action) {
		action.getContents().add(tInteractions.getInteractionEnablementFairDeepRL(fairnessDegreeDistance));
	}

	@Override
	protected void createInteractions() {
		togetherAction.getContents().add(this.getTInteractions().create());
		togetherAction.getContents().add(this.getTInteractions().getTInteractionsFirstEnable().create());
		if (withPriority)
			togetherAction.getContents().add(this.getTInteractions().getTInteractionsFilterPriority().create());
	}

	public abstract double[][][] getWeights();

	public abstract double[] getBias();

	public abstract int getInputNetworkSize();

	public abstract int getHiddenLayerNetworkSize();

	public abstract int getOutputNetworkSize();

	public void setGoodReward(double goodReward) {
		this.goodReward = goodReward;
	}

	public void setBadReward(double badReward) {
		this.badReward = badReward;
	}

	public void setMinimumTraceLength(int minimumTraceLength) {
		this.traceLengthIteration = minimumTraceLength;
	}

	public void setCapacityReplay(int capacityReplay) {
		this.capacityReplay = capacityReplay;
	}

	public void setEpoch(int epoch) {
		this.epoch = epoch;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public void setNeuronsHidden(int neuronsHidden) {
		this.numberOfNeuronsHidden = neuronsHidden;
	}

	public void setProbaRandom(double probaRandomExploration) {
		this.probabilityRandom = probaRandomExploration;
	}

	public void setResetHistoryPeriod(int resetHistoryPeriod) {
		this.numberResetHistoryTime = resetHistoryPeriod;
	}

	public void setEpisode(int episodes) {
		this.numberEpisodes = episodes;
	}

	public void setSampleCapacityPercentage(int sampleCapacityPercentage) {
		this.sampleCapacityPercentage = sampleCapacityPercentage;
	}

	public void setFairnessDegreeDistance(int fairnessDegreeDistance) {
		this.fairnessDegreeDistance = fairnessDegreeDistance;
	}
	
	public void setHiddenCount(int hiddenCound) {
		this.numberOfNeuronsHidden = hiddenCound;
	}

}
