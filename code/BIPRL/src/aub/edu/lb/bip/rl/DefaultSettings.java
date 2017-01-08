package aub.edu.lb.bip.rl;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;

public class DefaultSettings {
	
	// Default Settings for Value Iteration
	public final static double gamma = 0.9;
	public final static double badReward = -1;
	public final static double goodReward = 1;
	public final static int initialUtility = 0;
	public final static int DefaultMaxIteration = 100000;
	
	// Default Settings for Deep Reinforcement Learning
	final static int defaultCapacityReplay = 100;
	final static int defaultNumberEpisodes = 10; // 10
	final static double defaultProbabilityRandom = 0.5;
	final static int minimumTraceLengthIteration = 15;
	final static int defaultNumberOfNeuronsHidden = 10; // 100
	final static int defaultSampleCapacityPercentage = 50; 
	final static int defaultResetHistoryTime = 2;
	final static ActivationFunction defaultActivationFunction = new ActivationSigmoid();
	final static ActivationFunction defaultActivationFunctionOuter = new ActivationLinear();

	final public static double EPS = 1e-100;
	final public static int EPOCH = 1; // 1000
}
