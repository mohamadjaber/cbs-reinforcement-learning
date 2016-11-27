package aub.edu.lb.tests;

import java.io.FileNotFoundException;

import aub.edu.lb.bip.model.TCompound;
import aub.edu.lb.bip.model.TCompoundReinforcementLearning;
import aub.edu.lb.bip.model.TGenerator;

public class TestCodeGeneration {
	
	
	public static void main(String[] args) throws FileNotFoundException {
		TCompound tCompound = new TCompoundReinforcementLearning("bip-files/dining.bip", "bench/badStates");
		new TGenerator(tCompound, "/Users/jaber/Desktop/mainrl.cpp");
	}
}
