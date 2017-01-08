package aub.edu.lb.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import aub.edu.lb.bip.api.Helper;
import aub.edu.lb.bip.rl.DeepReinforcementLearning;
import aub.edu.lb.bip.rl.DefaultSettings;
import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.GlobalState;

public class TestDeepRL2 {

	public static void main(String[] args) {


		Scanner in = new Scanner(System.in);

		while (true) {
			DeepReinforcementLearning deepRL = new DeepReinforcementLearning("bip-files/dining.bip", "bench/badStates");
			GlobalState currentState = deepRL.getCompound().getInitialState();

			System.out.println("Enter Trace size: ");
			int traceSize = in.nextInt();
			if (traceSize <= 0)
				break;
			for (int t = 0; t < traceSize; t++) {
				double[] output = deepRL.getOutput(currentState.getIds());
				List<BIPInteraction> enabledInteractions = deepRL.getCompound().getEnabledInteractions(currentState);

				if (enabledInteractions == null || enabledInteractions.size() == 0) {
					System.out.println("enter deadlock state....");
					System.exit(0);
				}

				double maxOutput = -Double.POSITIVE_INFINITY;
				BIPInteraction selectedInteraction = null;
				
				List<BIPInteraction> selectedInteractions = new ArrayList<BIPInteraction>();
				
				for (BIPInteraction interaction : enabledInteractions) {
					int id = interaction.getId();
					if (output[id] > maxOutput) {
						maxOutput = output[id];
						selectedInteraction = interaction;
					}
				}
				
				for (BIPInteraction interaction : enabledInteractions) {
					int id = interaction.getId();
					if (Math.abs(output[id] - maxOutput) < DefaultSettings.goodReward) {
						selectedInteractions.add(interaction);
					}
				}
					
				int random = Helper.random(0, selectedInteractions.size());
				selectedInteraction = selectedInteractions.get(random);
				System.out.println("--------------");
				System.out.println("select interaction " + selectedInteraction.getId() + " -> " + selectedInteraction);
				currentState = deepRL.getCompound().next(currentState, selectedInteraction);
			}
		}
		in.close();
	}

}
