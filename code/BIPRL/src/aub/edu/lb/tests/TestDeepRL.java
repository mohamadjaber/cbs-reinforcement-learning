package aub.edu.lb.tests;


import aub.edu.lb.bip.model.TCompound;
import aub.edu.lb.bip.model.TCompoundNormal;
import aub.edu.lb.bip.rl.DeepReinforcementLearning;
import aub.edu.lb.model.Compound;

public class TestDeepRL {
	
	public static void main(String[] args) {
		TCompound tCompound = new TCompoundNormal("bip-files/dining.bip");
		Compound compound = tCompound.getCompound();
		int globalCounter = 0; 
		int numberOfBench = 100; 
		for(int i = 1; i <= numberOfBench; i++) {
			DeepReinforcementLearning deepRL = new DeepReinforcementLearning(compound, "bench/badStates"); 
			double[] output1 = deepRL.getOutput(new double[]{1.0, 1.0, 0.0, 1.0, 0.0, 1.0}); 
			double[] output2 = deepRL.getOutput(new double[]{0, 1, 1, 1, 1, 0}); 
			double[] output3 = deepRL.getOutput(new double[]{1, 0, 1, 0, 1, 1}); 
	
			//System.out.println(Arrays.toString(output1));
			//System.out.println(Arrays.toString(output2));
			//System.out.println(Arrays.toString(output3));
			
			int counter = output1[3] > output1[4]? 1 : 0; 
			counter = output2[0] < output2[5]? counter + 1 : counter; 
			counter = output3[1] > output3[2]? counter + 1 : counter; 
			System.out.printf("Bench %d -> %.2f%%\n", i , (100.0 * counter) / 3.0);
			globalCounter += counter; 
			
			//if(output1[3] > output1[4] && output2[0] < output2[5] && output3[1] > output3[2]) 
			//	System.out.println("good");
			//else {
			//	System.out.println("bad");
			//}
		}
		System.out.println("--------------------------------");
		System.out.printf("Average: %.2f", globalCounter * 100.0 / ( 3 * numberOfBench) );
	//	System.out.println(Arrays.toString(deepRL.getOutput(new double[]{0, 0, 0, 0, 0, 0})));
	
		
	}
	/*
	 * [1.0E-20, 1.0E-20, 1.0, 1.0, 1.0E-20, 1.0, 1.0, 1.0, 1.0E-20]
[1.0E-20, 1.0E-20, 1.0, 1.0, 1.0E-20, 1.0, 1.0, 1.0, 1.0]
[1.0E-20, 1.0E-20, 1.0, 1.0, 1.0E-20, 1.0, 1.0, 1.0, 1.0E-20]
[1.0E-20, 1.0E-20, 1.0, 1.0, 1.0, 1.0, 1.0E-20, 1.0, 1.0E-20]
	 */

}
