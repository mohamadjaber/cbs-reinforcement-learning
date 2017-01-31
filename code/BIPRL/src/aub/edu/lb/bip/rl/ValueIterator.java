package aub.edu.lb.bip.rl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import aub.edu.lb.bip.model.TCompoundReinforcementLearning;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;

/**
 * 
 * @author Jaber
 *
 */
public class ValueIterator extends TCompoundReinforcementLearning {

	private final static double EPS = DefaultSettings.EPS;

	public double[][] qValue; // TODO hash map in case of sparse cases...
	protected double[] utility;
	protected double[] reward;

	public int numberStates;
	public int numberActions;

	private Set<String> badStatesNames;

	private boolean debug = true;
	PrintStream ps = System.out;

	public ValueIterator(String bipFile, String badStateFile) {
		this(bipFile, badStateFile, DefaultSettings.DefaultMaxIteration, DefaultSettings.gamma,
				DefaultSettings.badReward, DefaultSettings.goodReward, DefaultSettings.initialUtility);
	}

	public double getQValue(int i, int j) {
		return qValue[i][j];
	}

	@Override
	public void compute() {
		if (this.debug)
			printDebugOptions();
		initialize();
		setTogetherAction();
	}

	public ValueIterator(String bipFile, String badStateFile, int maxIteration, double gamma, double badReward,
			double goodReward, int initialUtility) {
		super(bipFile, badStateFile);
		this.gamma = gamma;
		this.badReward = badReward;
		this.goodReward = goodReward;
		this.initialUtility = initialUtility;
		this.maxIteration = maxIteration;
	}

	private void initialize() {
		this.numberStates = (int) transitionSystem.getNumberStates();
		this.numberActions = transitionSystem.getCompound().getInteractions().size();
		this.utility = new double[numberStates];
		this.reward = new double[numberStates];
		this.badStatesNames = new HashSet<String>();
		qValue = new double[numberStates][numberActions];
		computeReward();
		computeQValues();
	}

	private void computeReward() {
		Arrays.fill(reward, goodReward);
		try {
			Scanner in = new Scanner(new File(badStateFile));
			while (in.hasNextLine()) {
				String badState = in.nextLine().replaceAll("\\s", "");
				Integer stateId = transitionSystem.getStateId(badState);
				if (stateId != null) { // otherwise the bad state is not reachable
					badStatesNames.add(badState);
					reward[stateId] = badReward;
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void computeQValues() {
		Arrays.fill(utility, initialUtility);
		int iteration = 0;
		double error = EPS + 1;
		while (iteration++ < maxIteration && error > EPS) {
			error = Double.MIN_VALUE;
			for (int i = 0; i < numberStates; i++) {
				KripkeState state = transitionSystem.getState(i);

				// to avoid incrementing q value in case bad state is connected
				// to good state
				// Haitham suggested to remove it but increase/decrease good/bad
				// reward
				// if(badStatesNames.contains(state.getState().toString())) {
				// continue; }

				double maxUtility = Double.MIN_VALUE;
				for (Transition t : state.getTransitions()) {
					int actionId = t.getLabel().getId();
					KripkeState nextState = t.getEndState();
					double updateQValue = reward[nextState.getId()] + gamma * utility[nextState.getId()];
					error = Math.max(error, Math.abs(updateQValue - qValue[i][actionId]));
					qValue[i][actionId] = updateQValue;
					maxUtility = Math.max(maxUtility, qValue[i][actionId]);
				}
				utility[i] = maxUtility;
			}
		}
		System.out.println(iteration + " iterations are needed to converge for EPS " + EPS);
	}

	private void printDebugOptions() {
		ps.println("Configuration...");
		ps.println("Good reward = " + this.goodReward);
		ps.println("Bad reward = " + this.badReward);
		ps.println("Max iteration bound = " + this.maxIteration);
	}

	public void printDebug() {
		for (int i = 0; i < numberStates; i++) {
			KripkeState state = transitionSystem.getState(i);
			System.out.println("Utility of " + state.getState() + " is" + utility[i]);
			for (Transition t : state.getTransitions()) {
				System.out.println(
						"q value to action " + t.getLabel().toString() + " is " + qValue[i][t.getLabel().getId()]);
			}
			System.out.println("------------------------------------------------------");
		}
	}
}
