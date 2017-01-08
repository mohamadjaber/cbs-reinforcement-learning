package aub.edu.lb.tests;



import aub.edu.lb.bip.rl.DeepReinforcementLearning;

public class TestDeepRL {
	
	public static void main(String[] args) {
		int globalCounter = 0; 
		int numberOfBench = 100; 
		for(int i = 1; i <= numberOfBench; i++) {
			DeepReinforcementLearning deepRL = new DeepReinforcementLearning("bip-files/dining.bip", "bench/badStates"); 
			deepRL.compute();
			double[] output1 = deepRL.getOutput(new double[]{1.0, 1.0, 0.0, 1.0, 0.0, 1.0}); 
			double[] output2 = deepRL.getOutput(new double[]{0, 1, 1, 1, 1, 0}); 
			double[] output3 = deepRL.getOutput(new double[]{1, 0, 1, 0, 1, 1}); 

			
			int counter = output1[3] > output1[4]? 1 : 0; 
			counter = output2[0] < output2[5]? counter + 1 : counter; 
			counter = output3[1] > output3[2]? counter + 1 : counter; 
			System.out.printf("Bench %d -> %.2f%%\n", i , (100.0 * counter) / 3.0);
			globalCounter += counter; 
		}
		System.out.println("--------------------------------");
		System.out.printf("Average: %.2f", globalCounter * 100.0 / ( 3 * numberOfBench) );
	
		
	}
}
