package aub.edu.lb.encog.helper;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class EncogHelper {

	/**
	 * PRE: all layers has bias except the output layer
	 */
	public static BasicNetwork copyBasicNetwork(BasicNetwork network) {
		BasicNetwork copyNetwork = new BasicNetwork();
		int numberLayers = network.getLayerCount();

		for (int i = 0; i < numberLayers - 1; i++) {
			copyNetwork.addLayer(new BasicLayer(network.getActivation(i), true,
					network.getStructure().getLayers().get(i).getNeuronCount()));
		}
		copyNetwork.addLayer(new BasicLayer(network.getActivation(numberLayers - 1), false,
				network.getStructure().getLayers().get(numberLayers - 1).getNeuronCount()));
		
		copyNetwork.getStructure().finalizeStructure();
		copyWeights(network, copyNetwork);
		return copyNetwork;

	}

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
			for (int neuronFrom = 0; neuronFrom < fromNetwork.getLayerTotalNeuronCount(i); neuronFrom++) {
				for (int neuronTo = 0; neuronTo < fromNetwork.getLayerTotalNeuronCount(i + 1); neuronTo++) {
					toNetwork.setWeight(i, neuronFrom, neuronTo, fromNetwork.getWeight(i, neuronFrom, neuronTo));
				}
			}
		}
	}
}
