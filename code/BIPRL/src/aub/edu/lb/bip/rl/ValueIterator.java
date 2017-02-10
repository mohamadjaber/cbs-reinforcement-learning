package aub.edu.lb.bip.rl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

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

	private BadStates badStates;

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
		this.badStates = new BadStates();
		qValue = new double[numberStates][numberActions];
		computeReward();
		computeQValues();
	}

	private void computeReward() {
		Arrays.fill(reward, goodReward);
		try {
			Scanner in = new Scanner(new File(badStateFile));
			while (in.hasNextLine()) {
				badStates.addBadState(new BadState(in.nextLine()));
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
		double error = 1 + EPS;
		while (iteration++ < maxIteration && error > EPS) {
			error = -Double.POSITIVE_INFINITY;

			for (int i = 0; i < numberStates; i++) {
				KripkeState state = transitionSystem.getState(i);

				//FIXME
				
				if (state.getTransitions().size() == 0) {
					if (badStates.isBadState(state)) {
						utility[state.getId()] = badReward;
					} else {
						utility[state.getId()] = goodReward;
					}
					continue;
				}

				if(badStates.isBadState(state)) continue;
				
				double maxUtility = -Double.POSITIVE_INFINITY;
				for (Transition t : state.getTransitions()) {
					int actionId = t.getLabel().getId();
					KripkeState nextState = t.getEndState();
					double reward = badStates.isBadState(nextState) ? badReward : goodReward;
					double updateQValue = reward + gamma * utility[nextState.getId()];
					error = Math.max(error, Math.abs(updateQValue - qValue[state.getId()][actionId]));
					qValue[state.getId()][actionId] = updateQValue;
					maxUtility = Math.max(maxUtility, qValue[state.getId()][actionId]);
				}
				utility[state.getId()] = maxUtility;
			}
		}
		System.out.println(iteration + " iterations are needed to converge for EPS " + EPS);
	}

	private void printDebugOptions() {
		ps.println("Configuration...");
		ps.println("Good reward = " + this.goodReward);
		ps.println("Bad reward = " + this.badReward);
		ps.println("Max iteration bound = " + this.maxIteration);
		ps.println("Discount factor = " + this.gamma);
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
