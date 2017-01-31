package aub.edu.lb.tests;

import java.io.FileNotFoundException;

import aub.edu.lb.bip.model.TCompound;
import aub.edu.lb.bip.model.TGenerator;
import aub.edu.lb.bip.rl.DeepReinforcementLearning;

public class TestCodeGeneration {
	
	
	public static void main(String[] args) throws FileNotFoundException {
		TCompound tCompound = new DeepReinforcementLearning("/Users/jaber/Desktop/bench-tmp/d5g.bip", "/Users/jaber/Desktop/bench-tmp/badStatesd5g");
		tCompound.compute();
		new TGenerator(tCompound, "/Users/jaber/Desktop/mainrl.cpp");
	}
}
