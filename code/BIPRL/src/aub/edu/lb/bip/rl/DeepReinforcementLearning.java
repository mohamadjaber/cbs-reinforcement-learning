package aub.edu.lb.bip.rl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import aub.edu.lb.bip.api.Helper;
import aub.edu.lb.bip.api.TransformationFunction;
import aub.edu.lb.bip.model.TCompoundDeepReinforcementLearning;
import aub.edu.lb.encog.helper.EncogHelper;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.Compound;
import aub.edu.lb.model.GlobalState;
import ujf.verimag.bip.Core.Interactions.Component;

public class DeepReinforcementLearning extends TCompoundDeepReinforcementLearning {

	private BasicNetwork networkCurrent; // teta
	private BasicNetwork networkHistory; // teta minus
	private List<TransitionReplay> memoryReplay;
	private BadStates badStates;
	
	private ActivationFunction activationFunction = DefaultSettings.defaultActivationFunction;
	private ActivationFunction activationFunctionOuter = DefaultSettings.defaultActivationFunctionOuter;
	
	private boolean debug = true; 
	PrintStream ps = System.out; 
	
	double[][][] weights;
	double[] bias;
	
	public DeepReinforcementLearning(String bipFile, String fileBadStates,
			double goodReward, double badReward, 
			int episodes, int epoch, int neuronsHidden,
			int capacityReplay, 
			double probaRandomExploration,
			int minimumTraceLength,
			int sampleCapacityPercentage, int resetHistoryPeriod, 
			double gamma, boolean debug, int fairnessDegreeDistance) {
			super(bipFile, fileBadStates, goodReward, badReward, episodes, epoch,
					neuronsHidden, capacityReplay, probaRandomExploration, minimumTraceLength, sampleCapacityPercentage,
					resetHistoryPeriod, gamma, debug, fairnessDegreeDistance);
	}
	

	
	@Override
	public void compute() {
		this.badStates = new BadStates();
		fillBadStates();
		initializeTraceLengthIteration();
		
		if(this.debug) printDebugOptions();
		
		initializeNeuralNetworks();
		memoryReplay = new LinkedList<TransitionReplay>();
		
		if(this.debug) ps.println("\n\nStart training:");

		trainEpisodes();
		
		setTogetherAction();
	}
	
	public DeepReinforcementLearning(String bipFile, String fileBadStates) {
		super(bipFile, fileBadStates, 
				DefaultSettings.goodReward, DefaultSettings.badReward,
				DefaultSettings.defaultNumberEpisodes, 
				DefaultSettings.EPOCH, 
				DefaultSettings.defaultNumberOfNeuronsHidden, 
				DefaultSettings.defaultCapacityReplay, 
				DefaultSettings.defaultProbabilityRandom, 
				DefaultSettings.minimumTraceLengthIteration, 
				DefaultSettings.defaultSampleCapacityPercentage, 
				DefaultSettings.defaultResetHistoryTime,
				DefaultSettings.gamma, true, -1);
	}
	
	private void printDebugOptions() {
		ps.println("Training configuration...");
		ps.println("Number episodes = "+ this.numberEpisodes);
		ps.println("Good reward = "+ this.goodReward);
		ps.println("Bad reward = "+ this.badReward);
		ps.println("Number of neurons hidden layer = " + this.numberOfNeuronsHidden);
		ps.println("Epoch training = "+ this.epoch);

		ps.println("Trace length iteration = "+ this.traceLengthIteration);
		ps.println("Capacity replay = "+ this.capacityReplay);
		ps.println("Probability random exploration = "+ this.probabilityRandom);
		ps.println("Reset history period = "+ this.numberResetHistoryTime);
		ps.println("Sample capacity percentage = "+ this.sampleCapacityPercentage);
		
		if(this.fairnessDegreeDistance <= 0) {
			ps.println("Fairness = no");
		} else {
			ps.println("Fairness = " + this.fairnessDegreeDistance);
		}
	}
	
	private void trainEpisodes() {
		for (int i = 1; i <= numberEpisodes; i++) {
			ps.println("Training: episode " + i + " / " + this.numberEpisodes);
			trainEpisode();
		}
		setWeights();
	}

	private void trainEpisode() {
		GlobalState currentState = compound.getInitialState();
		
		for (int t = 1; t <= traceLengthIteration; t++) {
			/* select interaction */
			BIPInteraction interaction = pickInteraction(currentState);
			
			/* if deadlock state break the trace and move to the next episode */
			if(interaction == null) break;

			/* execute interaction */
			GlobalState nextState = compound.next(currentState, interaction);

			/*
			 * store in memoryReplay the transition replay 
			 * <currentState, interaction, reward nextState, nextState>
			 */
			boolean isBadStateNext = badStates.isBadState(nextState);
			double reward = isBadStateNext ? badReward : goodReward;
			TransitionReplay transition = new TransitionReplay(currentState, interaction, nextState, reward);
			
			memoryReplay.add(transition);
			if(memoryReplay.size() > capacityReplay) memoryReplay.remove(0);
			
			train();
		
			/* every c steps reset teta minus */
			if((t % numberResetHistoryTime) == 0) {
				EncogHelper.copyWeights(networkCurrent, networkHistory);
			}
			
			/* if next state is bad break and move to the next episode */
			if(isBadStateNext) break;
			
			currentState = nextState; 
		}
	}
	
	private void train() {
		List<TransitionReplay> miniBatch = sampleMiniBatch();
		if(miniBatch.size() != 0) train(miniBatch);
	}
	
	private void train(List<TransitionReplay> miniBatch) {
		double[][] input = new double[miniBatch.size()][];
		double[][] output = new double[miniBatch.size()][];
		int i = 0;
		for(TransitionReplay t: miniBatch) {
			output[i] = generateTrainingPoint(t);
			input[i] = t.getFromState().getIds();
			i++;
		}
		EncogHelper.learning(networkCurrent, input, output, epoch, DefaultSettings.EPS);
	}
	
	private double[] generateTrainingPoint(TransitionReplay transition) {
		boolean isBadState = badStates.isBadState(transition.getToState()); 
		double[] outputNetworkCurrentState = EncogHelper.forwardPropagation(networkHistory, transition.getFromState().getIds());
		List<BIPInteraction> enabledInteractions = compound.getEnabledInteractions(transition.getToState());

		if(isBadState || enabledInteractions == null || enabledInteractions.size() == 0) {
			outputNetworkCurrentState[transition.getInteraction().getId()] = transition.getReward();
		} else {	
			double[] outputNetworkNextState = EncogHelper.forwardPropagation(networkHistory, transition.getToState().getIds());
			double maxOutput = -Double.POSITIVE_INFINITY;
			for (BIPInteraction interaction : enabledInteractions) {
				int id = interaction.getId();
				if (outputNetworkNextState[id] > maxOutput) {
					maxOutput = outputNetworkNextState[id];
				}
			}
			outputNetworkCurrentState[transition.getInteraction().getId()] = transition.getReward() + gamma * maxOutput;
		}
		return outputNetworkCurrentState;
	}
	
	private List<TransitionReplay> sampleMiniBatch() {
		List<TransitionReplay> miniBatch = new ArrayList<TransitionReplay>();
		int sizeMiniBatch = (int) Math.ceil((1.0 * memoryReplay.size() * sampleCapacityPercentage) / 100); 
		int[] miniBatchIndices = Helper.generateRandomIndices(sizeMiniBatch, memoryReplay.size());
		for(int i = 0; i < miniBatchIndices.length; i++) {
			miniBatch.add(memoryReplay.get(miniBatchIndices[i]));
		}
		return miniBatch;
		
	}

	private void fillBadStates() {
		try {
			Scanner in = new Scanner(new File(badStateFile));
			while (in.hasNextLine()) {
				badStates.addBadState(new BadState(in.nextLine()));
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param state
	 * @return pick random interaction with probability @probabilityRandom pick
	 *         interaction w.r.t. exploitation, i.e., a_select = argmax(Q(s,a;
	 *         teta)) where a in enable interaction. Return null if the input
	 *         state is deadlock state
	 */
	private BIPInteraction pickInteraction(GlobalState state) {
		List<BIPInteraction> enabledInteractions = compound.getEnabledInteractions(state);
		if (enabledInteractions == null || enabledInteractions.size() == 0)
			return null; // deadlock state
		if (!isExploit()) { // random Selection
			int indexInteraction = Helper.random(0, enabledInteractions.size());
			return enabledInteractions.get(indexInteraction);
		} else {
			double[] outputNetwork = EncogHelper.forwardPropagation(networkCurrent, state.getIds());
			BIPInteraction maxInteraction = null;
			double maxOutput = -Double.POSITIVE_INFINITY;
			for (BIPInteraction interaction : enabledInteractions) {
				int id = interaction.getId();
				if (outputNetwork[id] >= maxOutput) {
					maxInteraction = interaction;
					maxOutput = outputNetwork[id];
				}
			}
			return maxInteraction;
		}
	}
	
	
	public double[] getOutput(double[] state) {
		return EncogHelper.forwardPropagation(networkCurrent, state);
	}
	
	
	private boolean isExploit() {
		return Math.random() > probabilityRandom;
	}

	/**
	 * 
	 */
	private void initializeNeuralNetworks() {
		networkCurrent = new BasicNetwork();
		networkCurrent.addLayer(new BasicLayer(null, true, compound.stateLength()));
		networkCurrent.addLayer(new BasicLayer(activationFunction, true, numberOfNeuronsHidden));
		networkCurrent.addLayer(new BasicLayer(activationFunctionOuter, false, compound.getInteractions().size()));
		networkCurrent.getStructure().finalizeStructure();
		networkCurrent.reset();
		networkHistory = (BasicNetwork) networkCurrent.clone();
	}

	public Compound getCompound() {
		return compound;
	}
	
	public int getInputNetworkSize() {
		return compound.stateLength() + 1; // one for bias
	}
	
	public int getHiddenLayerNetworkSize() {
		return numberOfNeuronsHidden + 1; // one for bias
	}
	
	public int getOutputNetworkSize() {
		return compound.getInteractions().size();
	}
	

	/**
	 * TODO: Tweak it w.r.t syntax Diameter or number of locations
	 */
	public void initializeTraceLengthIteration() {
		traceLengthIteration = DefaultSettings.minimumTraceLengthIteration;
		for(Component component: compound.getCompoundType().getSubcomponent()) {
			traceLengthIteration = Math.max(traceLengthIteration, 
					(int) TransformationFunction.getNumberStates(component));
		}
	}
	
	public void setWeights() {
		weights = new double[networkCurrent.getLayerCount() - 1][][];
		bias = new double[networkCurrent.getLayerCount() - 1];

		int numberLayers = networkCurrent.getLayerCount();
		for (int i = 0; i < numberLayers - 1; i++) {
			weights[i] = new double[networkCurrent.getLayerTotalNeuronCount(i)][networkCurrent.getLayerTotalNeuronCount(i + 1)];
			bias[i] = networkCurrent.getLayerBiasActivation(i);
			// from layer getLayerTotalNeuronCount
			for (int neuronFrom = 0; neuronFrom < networkCurrent.getLayerTotalNeuronCount(i); neuronFrom++) {
				// to layer getLayerNeuronCount (from is not connected to the
				// bias neuron and,
				// the bias weight is the last weight on each layer
				for (int neuronTo = 0; neuronTo < networkCurrent.getLayerNeuronCount(i + 1); neuronTo++) {
					weights[i][neuronFrom][neuronTo] = networkCurrent.getWeight(i, neuronFrom, neuronTo);
				}
			}
		}
	}
	
	public void printWeights(BasicNetwork fromNetwork) {
		int numberLayers = fromNetwork.getLayerCount();
		for (int i = 0; i < numberLayers - 1; i++) {
			System.out.println("from layer " + i + " to layer " + (i + 1));
			System.out.println("layer bias = " + fromNetwork.getLayerBiasActivation(i));
			// from layer getLayerTotalNeuronCount
			for (int neuronFrom = 0; neuronFrom < fromNetwork.getLayerTotalNeuronCount(i); neuronFrom++) {
				// to layer getLayerNeuronCount (from is not connected to the
				// bias neuron and,
				// the bias weight is the last weight on each layer
				for (int neuronTo = 0; neuronTo < fromNetwork.getLayerNeuronCount(i + 1); neuronTo++) {
					System.out.println("from " + neuronFrom + " -- to " + neuronTo + " --> "
							+ fromNetwork.getWeight(i, neuronFrom, neuronTo));
				}
			}
			System.out.println("-------------------------------------");
		}
	}

	
	public double[][][] getWeights() {
		return weights; 
	}
	
	public double[] getBias() {
		return bias; 
	}
}
