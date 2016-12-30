package aub.edu.lb.tests;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import aub.edu.lb.bip.rl.DefaultSettings;
import aub.edu.lb.encog.helper.EncogHelper;

public class TestNeuralNetworkSimple {
	private static BasicNetwork neuralNetwork; 
	final static ActivationFunction activationFunction = new ActivationSigmoid();
	final static ActivationFunction activationFunctionOuter = new ActivationLinear();
	final static int numberOfNeuronsHidden = 1; 
	final static int inputLength = 1; 
	final static int outputLength = 2;
	final static int maxEpoch = 100;
	final static int goodReward = 1;
	final static int badReward = -1;
	
	public static void main(String[] args) {
		initializeNeuralNetworks();
		
		double[][] inputTraining = { {0} };
		double[][] outputTraining = { {goodReward, badReward} };
		
		EncogHelper.learning(neuralNetwork, inputTraining, outputTraining, maxEpoch, DefaultSettings.EPS);
		
		double[] outputTest = EncogHelper.forwardPropagation(neuralNetwork, inputTraining[0]);
		
		System.out.println("-----------------------------------");
		System.out.println("expected: " + outputTraining[0][0] + " - " + outputTraining[0][1]);
		System.out.println("output: " + outputTest[0] + " - " + outputTest[1]);
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
