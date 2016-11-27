package aub.edu.lb.bip.rl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.kripke.Transition;


/**
 * 
 * @author Jaber & Nassar
 *
 */
public class ValueIterator implements ReinforcementLearning {

	private final static double EPS = 1E-50;
	private final static int MAXITERATION = 10000000;

	public double[][] qValue; // TODO hash map in case of sparse cases...
	protected double[] utility;
	protected int[] reward;
	public Kripke transitionSystem;

	public final int numberStates;
	public final int numberActions;
	
	private Set<String> badStatesNames; 
	
	

	public ValueIterator(Kripke transitionSystem, String fileBadStates) {
		this.transitionSystem = transitionSystem;
		this.numberStates = (int) transitionSystem.getNumberStates();
		this.numberActions = transitionSystem.getCompound().getInteractions().size();
		this.utility = new double[numberStates];
		this.reward = new int[numberStates];
		this.badStatesNames = new HashSet<String>();
		qValue = new double[numberStates][numberActions];
		computeReward(fileBadStates);
		computeQValues();
	}

	private void computeReward(String fileBadStates) {
		Arrays.fill(reward, goodReward);
		try {
			Scanner in = new Scanner(new File(fileBadStates));
			while (in.hasNextLine()) {
				String badState = in.nextLine();
				int stateId = transitionSystem.getStateId(badState);
				badStatesNames.add(badState);
				reward[stateId] = badReward;
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
		while (iteration++ < MAXITERATION && error > EPS) {
			error = Double.MIN_VALUE;
			for (int i = 0; i < numberStates; i++) {
				KripkeState state = transitionSystem.getState(i);
				
				// to avoid incrementing q value in case bad state is connected to good state
				// Haitham suggested to remove it but increase/decrease good/bad reward
				// if(badStatesNames.contains(state.getState().toString())) { continue; }
	
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
