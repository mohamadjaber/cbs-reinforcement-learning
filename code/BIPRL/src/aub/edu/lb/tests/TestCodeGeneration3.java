package aub.edu.lb.tests;

import java.io.FileNotFoundException;


import aub.edu.lb.bip.model.TGenerator;
import aub.edu.lb.bip.rl.DeepReinforcementLearning;

public class TestCodeGeneration3 {
	
	
	public static void main(String[] args) throws FileNotFoundException {
		DeepReinforcementLearning tCompound = new DeepReinforcementLearning("bip-files/dining.bip", "bench/badStates");
		tCompound.setFairnessDegreeDistance(3);
		
		new TGenerator(tCompound, "/Users/jaber/Desktop/maindeeprl.cpp");
	}
}
