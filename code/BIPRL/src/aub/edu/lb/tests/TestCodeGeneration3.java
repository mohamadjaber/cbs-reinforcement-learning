package aub.edu.lb.tests;

import java.io.FileNotFoundException;

import aub.edu.lb.bip.model.TCompound;
import aub.edu.lb.bip.model.TCompoundDeepReinforcementLearning;
import aub.edu.lb.bip.model.TGenerator;

public class TestCodeGeneration3 {
	
	
	public static void main(String[] args) throws FileNotFoundException {
		TCompound tCompound = new TCompoundDeepReinforcementLearning("bip-files/dining.bip", "bench/badStates", true);
		new TGenerator(tCompound, "/Users/jaber/Desktop/maindeeprl.cpp");
	}
}
