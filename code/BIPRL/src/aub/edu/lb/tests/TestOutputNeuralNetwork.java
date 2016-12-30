package aub.edu.lb.tests;

import java.util.Arrays;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import aub.edu.lb.bip.rl.DefaultSettings;
import aub.edu.lb.encog.helper.EncogHelper;

public class TestOutputNeuralNetwork {
	private static BasicNetwork neuralNetwork;
	final static ActivationFunction activationFunction = new ActivationSigmoid();
	final static ActivationFunction activationFunctionOuter = new ActivationLinear();
	final static int numberOfNeuronsHidden = 4;
	final static int inputLength = 6;
	final static int outputLength = 9;
	final static int maxEpoch = 1000;
	final static int goodReward = 1;
	final static int badReward = -1;

	public static void main(String[] args) {
		initializeNeuralNetworks();

		double[][] inputTraining = { { 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0, 1 }, { 0, 1, 0, 1, 0, 0 },
				{ 0, 0, 1, 0, 1, 0 }, { 2, 0, 0, 1, 0, 1 }, { 0, 2, 0, 1, 1, 0 }, { 0, 0, 2, 0, 1, 1 },
				{ 2, 0, 1, 1, 1, 1 }, { 1, 2, 0, 1, 1, 1 }, { 0, 1, 2, 1, 1, 1 }, { 1, 1, 0, 1, 0, 1 },
				{ 0, 1, 1, 1, 1, 0 }, { 1, 0, 1, 0, 1, 1 }, { 1, 1, 1, 1, 1, 1 } };

		double[][] outputTraining = { { 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, -1, 1, 1, 1, 1 },
				{ -1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, -1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1 } };

		EncogHelper.learning(neuralNetwork, inputTraining, outputTraining, maxEpoch, DefaultSettings.EPS);

		/**********/
		printWeights(neuralNetwork);
		setWeights(neuralNetwork);
		System.out.println(weights[1][0][2]);
		System.out.println(bias[1]);
		System.out.println(weights[1][0].length);
		/**********/
		
		for (int i = 0; i < inputTraining.length; i++) {
			double[] output = EncogHelper.forwardPropagation(neuralNetwork, inputTraining[i]);
			System.out.println("-----------------------------------");

			System.out.println("expected: ");
			outputArray(outputTraining[i]);
			System.out.println("output: ");
			outputArray(output);
			System.out.println("manual: ");
			outputArray(computeOutput(inputTraining[i]));
		}

		
		
	}

	private static void outputArray(double[] input) {
		System.out.printf("[%.1f", input[0]);
		for (int i = 1; i < input.length; i++)
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

	public static void printWeights(BasicNetwork fromNetwork) {
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

	static double[][][] weights;
	static double[] bias;

	public static void setWeights(BasicNetwork network) {
		weights = new double[network.getLayerCount() - 1][][];
		bias = new double[network.getLayerCount() - 1];

		int numberLayers = network.getLayerCount();
		for (int i = 0; i < numberLayers - 1; i++) {
			weights[i] = new double[network.getLayerTotalNeuronCount(i)][network.getLayerNeuronCount(i + 1)];
			bias[i] = network.getLayerBiasActivation(i);
			// from layer getLayerTotalNeuronCount
			for (int neuronFrom = 0; neuronFrom < network.getLayerTotalNeuronCount(i); neuronFrom++) {
				// to layer getLayerNeuronCount (from is not connected to the
				// bias neuron and,
				// the bias weight is the last weight on each layer
				for (int neuronTo = 0; neuronTo < network.getLayerNeuronCount(i + 1); neuronTo++) {
					weights[i][neuronFrom][neuronTo] = network.getWeight(i, neuronFrom, neuronTo);
				}
			}
		}
	}

	public static double[] computeOutput(double[] input) {
		int beforeLastLayerIndex = weights.length - 1;
		double[] outputTmp = Arrays.copyOf(input, input.length + 1);
		outputTmp[outputTmp.length - 1] = bias[0];

		for (int i = 0; i <= beforeLastLayerIndex; i++) {
			outputTmp = product(outputTmp, weights[i], i != beforeLastLayerIndex? bias[i + 1] : 0);
			if (i != beforeLastLayerIndex) {
				sigmoid(outputTmp);
				// outputTmp = Arrays.copyOf(outputTmp, outputTmp.length + 1);
				//outputTmp[outputTmp.length - 1] = bias[i + 1];
			}
		}
		return outputTmp;
	}

	public static double[] product(double[] vector, double[][] matrix, double bias) {
		double[] output = new double[matrix[0].length + 1];
		for (int j = 0; j < matrix[0].length; j++) {
			for (int i = 0; i < matrix.length; i++) {
				output[j] += vector[i] * matrix[i][j];
			}
			output[matrix[0].length] = bias;
		}
		return output;
	}

	private static void sigmoid(double[] x) {
		for (int i = 0; i < x.length - 1; i++) {
			x[i] = sigmoid(x[i]);
		}
	}

	private static double sigmoid(double x) {
		return (1.0 / (1 + Math.exp(-x)));
	}

}
