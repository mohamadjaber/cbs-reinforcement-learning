package aub.edu.lb.tests;

import aub.edu.lb.bip.api.TransformationFunction;
import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.model.Compound;
import ujf.verimag.bip.Core.Interactions.CompoundType;

public class TestModel {

	public static void main(String[] args) {
		CompoundType ct = TransformationFunction.parseBIPFile("bip-files/dining.bip");
		Compound compound = new Compound(ct);
		System.out.println(compound.getInitialState());
		
		Kripke transitionSystem = new Kripke(compound);

		for(KripkeState state: transitionSystem.getStates()) {
			System.out.println(state);
		}
	}

}
