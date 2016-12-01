package aub.edu.lb.tests;

import java.util.Arrays;

import aub.edu.lb.bip.model.TCompound;
import aub.edu.lb.bip.model.TCompoundNormal;
import aub.edu.lb.bip.rl.DeepReinforcementLearning;
import aub.edu.lb.model.Compound;

public class TestDeepRL {
	
	public static void main(String[] args) {
		TCompound tCompound = new TCompoundNormal("bip-files/dining.bip");
		Compound compound = tCompound.getCompound();
		DeepReinforcementLearning deepRL = new DeepReinforcementLearning(compound, "bench/badStates"); 
		
		System.out.println(Arrays.toString(deepRL.getOutput(new double[]{1, 1, 0, 1, 0, 1})));
		
		System.out.println(Arrays.toString(deepRL.getOutput(new double[]{0, 1, 1, 1, 1, 0})));
		
		System.out.println(Arrays.toString(deepRL.getOutput(new double[]{1, 0, 1, 0, 1, 1})));

		System.out.println(Arrays.toString(deepRL.getOutput(new double[]{0, 0, 0, 0, 0, 0})));
	
	}
	/*
	 * [1.0E-20, 1.0E-20, 1.0, 1.0, 1.0E-20, 1.0, 1.0, 1.0, 1.0E-20]
[1.0E-20, 1.0E-20, 1.0, 1.0, 1.0E-20, 1.0, 1.0, 1.0, 1.0]
[1.0E-20, 1.0E-20, 1.0, 1.0, 1.0E-20, 1.0, 1.0, 1.0, 1.0E-20]
[1.0E-20, 1.0E-20, 1.0, 1.0, 1.0, 1.0, 1.0E-20, 1.0, 1.0E-20]
	 */

}
