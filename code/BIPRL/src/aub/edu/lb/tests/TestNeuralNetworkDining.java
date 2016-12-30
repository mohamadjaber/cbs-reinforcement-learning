package aub.edu.lb.tests;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;


import aub.edu.lb.bip.rl.DefaultSettings;
import aub.edu.lb.encog.helper.EncogHelper;

public class TestNeuralNetworkDining {
	private static BasicNetwork neuralNetwork; 
	final static ActivationFunction activationFunction = new ActivationSigmoid();
	final static ActivationFunction activationFunctionOuter = new ActivationLinear();
	final static int numberOfNeuronsHidden = 200; 
	final static int inputLength = 6; 
	final static int outputLength = 9;
	final static int maxEpoch = 1000;
	final static int goodReward = 1;
	final static int badReward = -1;
	
	public static void main(String[] args) {
		initializeNeuralNetworks();
		
		double[][] inputTraining = { 
				{0, 0, 0, 0, 0, 0}, 
				{1, 0, 0, 0, 0, 1},
				{0, 1, 0, 1, 0, 0},
				{0, 0, 1, 0, 1, 0},
				{2, 0, 0, 1, 0, 1},
				{0, 2, 0, 1, 1, 0},
				{0, 0, 2, 0, 1, 1},
				{2, 0, 1, 1, 1, 1},
				{1, 2, 0, 1, 1, 1},
				{0, 1, 2, 1, 1, 1},
				{1, 1, 0, 1, 0, 1},
				{0, 1, 1, 1, 1, 0},
				{1, 0, 1, 0, 1, 1},
				{1, 1, 1, 1, 1, 1}};
		
		double[][] outputTraining = { 
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, -1, 1, 1, 1, 1},
				{-1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, -1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1}
			};
		
		EncogHelper.learning(neuralNetwork, inputTraining, outputTraining, maxEpoch, DefaultSettings.EPS);
		
		for(int i = 0; i < inputTraining.length; i++) {
			double[] output = EncogHelper.forwardPropagation(neuralNetwork, inputTraining[i]);
			System.out.println("-----------------------------------");
			
			System.out.println("expected: ");
			outputArray(outputTraining[i]);
			System.out.println("output: ");
			outputArray(output);
		}
	}
	
	private static void outputArray(double[] input) {
		System.out.printf("[%.1f", input[0]);
		for(int i = 1; i < input.length; i++)
			System.out.printf(", %.1f", input[i]);
		System.out.println("]");
	}
	
	private static void initializeNeuralNetworks() {
		neuralNetwork = new BasicNetwork();
		neuralNetwork.addLayer(new BasicLayer(null, true, inputLength));
		neuralNetwork.addLayer(new BasicLayer(activationFunction, true, numberOfNeuronsHidden));
		neuralNetwork.addLayer(new BasicLayer(activationFunctionOuter, false, outputLength));
		neuralNetwork.getStructure().finalizeStructure();
		neuralNetwork.reset();
	}

}
