package aub.edu.lb.tests;

import java.io.FileNotFoundException;

import aub.edu.lb.bip.model.TCompound;
import aub.edu.lb.bip.model.TCompoundNormal;
import aub.edu.lb.bip.model.TGenerator;

public class TestCodeGeneration2 {
	
	
	public static void main(String[] args) throws FileNotFoundException {
		TCompound tCompound = new TCompoundNormal("bip-files/dining.bip");
		new TGenerator(tCompound, "/Users/jaber/Desktop/main.cpp");
	}
}
