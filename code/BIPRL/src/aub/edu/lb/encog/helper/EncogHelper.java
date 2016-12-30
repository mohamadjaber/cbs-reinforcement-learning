package aub.edu.lb.encog.helper;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;


public class EncogHelper {

	/**
	 * PRE: two networks have the same structure
	 * 
	 * @param fromNetwork
	 * @param toNetwork
	 */
	public static void copyWeights(BasicNetwork fromNetwork, BasicNetwork toNetwork) {
		int numberLayers = fromNetwork.getLayerCount();
		for (int i = 0; i < numberLayers - 1; i++) {
			toNetwork.setLayerBiasActivation(i, fromNetwork.getLayerBiasActivation(i));
			// from layer getLayerTotalNeuronCount
			for (int neuronFrom = 0; neuronFrom < fromNetwork.getLayerTotalNeuronCount(i); neuronFrom++) {
				// to layer getLayerNeuronCount (from is not connected to the bias neuron and,
				// the bias weight is the last weight on each layer
				for (int neuronTo = 0; neuronTo < fromNetwork.getLayerNeuronCount(i + 1); neuronTo++) {
					toNetwork.setWeight(i, neuronFrom, neuronTo, fromNetwork.getWeight(i, neuronFrom, neuronTo));
				}
			}
		}
	}

	public static void learning(BasicNetwork network, double[][] input, double[][] output, int maxEpoch, double eps) {
		MLDataSet trainingSet = new BasicMLDataSet(input, output);
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		int epoch = 1;
		do {
			train.iteration();
			// System.out.println("after Epoch " + epoch + " --> " + train.getError());
			epoch++;
		} while (train.getError() > eps && epoch < maxEpoch);
		train.finishTraining();
	}

	/**
	 * PRE: state conforms with the input of the network
	 * 
	 * @param network
	 * @param state
	 * @return forward propagation
	 */
	public static double[] forwardPropagation(BasicNetwork network, double[] state) {
		BasicMLData input = new BasicMLData(state);
		return network.compute(input).getData();
	}

	
	/**
	 * 
	 * @param network
	 * @return
	 */
	public static String dumpWeightsVerbose(BasicNetwork network) {
		final StringBuilder result = new StringBuilder();

		for (int layer = 0; layer < network.getLayerCount() - 1; layer++) {
			int bias = 0;
			if (network.isLayerBiased(layer)) {
				bias = 1;
			}
			System.out.println("here " + network.getLayerNeuronCount(layer));
			for (int fromIdx = 0; fromIdx < network.getLayerNeuronCount(layer) + bias; fromIdx++) {
				for (int toIdx = 0; toIdx < network.getLayerNeuronCount(layer + 1); toIdx++) {
					String type1 = "", type2 = "";
					if (layer == 0) {
						type1 = "I";
						type2 = "H" + (layer) + ",";
					} else {
						type1 = "H" + (layer - 1) + ",";
						if (layer == (network.getLayerCount() - 2)) {
							type2 = "O";
						} else {
							type2 = "H" + (layer) + ",";
						}
					}
					// The bias weight is the last weight on each layer
					if (bias == 1 && (fromIdx == network.getLayerNeuronCount(layer))) {
						type1 = "bias";
					} else {
						type1 = type1 + fromIdx;
					}
					result.append(type1 + "-->" + type2 + toIdx + " : " + network.getWeight(layer, fromIdx, toIdx) + "\n");
				}
			}
		}

		return result.toString();
	}
}
