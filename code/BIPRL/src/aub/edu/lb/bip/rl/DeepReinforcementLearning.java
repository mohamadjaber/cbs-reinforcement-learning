package aub.edu.lb.bip.rl;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import aub.edu.lb.encog.helper.EncogHelper;
import aub.edu.lb.model.Compound;

public class DeepReinforcementLearning {
	
	private Compound compound;
	private BasicNetwork networkCurrent; // teta
	private BasicNetwork networkHistory; // teta minus
	private List<TransitionReplay> memoryReplay; 


	
	// configuration
	private int capacityReplay = DefaultSettings.defaultCapacityReplay; 
	private int numberEpisodes = DefaultSettings.defaultNumberEpisodes; 
	private double probabilityRandom = DefaultSettings.defaultProbabilityRandom;
	private int traceLengthIteration; 
	private int numberOfNeuronsHidden = DefaultSettings.defaultNumberOfNeuronsHidden; 
	private ActivationFunction activationFunction = DefaultSettings.defaultActivationFunction;
	
	public DeepReinforcementLearning(Compound compound) {
		this.compound = compound;
		initializeTraceLengthIteration();
		initializeNeuralNetworks();
		memoryReplay = new ArrayList<TransitionReplay>(capacityReplay);
		
	}
	
	
	/**
	 * 
	 */
	private void initializeNeuralNetworks() {
		networkCurrent = new BasicNetwork();
		networkCurrent.addLayer(new BasicLayer(null, true, compound.stateLength()));
		networkCurrent.addLayer(new BasicLayer(activationFunction, true, numberOfNeuronsHidden));
		networkCurrent.addLayer(new BasicLayer(activationFunction, false, compound.getInteractions().size()));
		networkCurrent.getStructure().finalizeStructure();
		networkCurrent.reset();
		networkHistory = EncogHelper.copyBasicNetwork(networkCurrent);
	}


	public Compound getCompound() {
		return compound;
	}

	
	/**
	 * TODO: Tweak it w.r.t syntax Diameter or number of locations
	 */
	public void initializeTraceLengthIteration() {
		traceLengthIteration = DefaultSettings.defaultTraceLengthIteration;
	}
}
