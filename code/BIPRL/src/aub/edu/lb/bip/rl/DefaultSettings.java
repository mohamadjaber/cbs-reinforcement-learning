package aub.edu.lb.bip.rl;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;

public class DefaultSettings {
	
	// Default Settings for Value Iteration
	final static double gamma = 0.9;
	final static int badReward = -10;
	final static int goodReward = 10;
	final static int initialUtility = 0; 
	
	// Default Settings for Deep Reinforcement Learning
	final static int defaultCapacityReplay = 100;
	final static int defaultNumberEpisodes = 100;
	final static double defaultProbabilityRandom = 0.1;
	final static int defaultTraceLengthIteration = 100;
	final static int defaultNumberOfNeuronsHidden = 10;
	final static ActivationFunction defaultActivationFunction = new ActivationSigmoid();

}
