package aub.edu.lb.bip.rl;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;

public class DefaultSettings {
	
	// Default Settings for Value Iteration
	final static double gamma = 0.9;
	final static double badReward = -10;
	final static double goodReward = 1;
	final static int initialUtility = 0; 
	
	// Default Settings for Deep Reinforcement Learning
	final static int defaultCapacityReplay = 100;
	final static int defaultNumberEpisodes = 100;
	final static double defaultProbabilityRandom = 0.2;
	final static int defaultTraceLengthIteration = 100;
	final static int defaultNumberOfNeuronsHidden = 100;
	final static int defaultSampleCapacityPercentage = 50; 
	final static int defaultResetHistoryTime = 10;
	final static ActivationFunction defaultActivationFunction = new ActivationSigmoid();
	final static ActivationFunction defaultActivationFunctionOuter = new ActivationLinear();

	
	final public static double EPS = 10E-200;
	final public static int EPOCH = 1000;
}
