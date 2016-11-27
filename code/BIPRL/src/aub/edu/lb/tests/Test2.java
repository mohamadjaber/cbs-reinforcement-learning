package aub.edu.lb.tests;

import java.io.FileNotFoundException;

import aub.edu.lb.bip.model.TCompound;
import aub.edu.lb.bip.model.TCompoundNormal;
import aub.edu.lb.model.Compound;
import aub.edu.lb.model.GlobalState;

public class Test2 {
	public static void main(String[] args) throws FileNotFoundException {
		TCompound tCompound = new TCompoundNormal("bip-files/dining.bip");
		Compound compound = tCompound.getCompound();
		GlobalState initialState = compound.getInitialState();
		System.out.println(initialState);
	}

}
