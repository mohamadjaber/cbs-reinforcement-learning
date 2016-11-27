package aub.edu.lb.tests;

import aub.edu.lb.bip.api.TransformationFunction;
import aub.edu.lb.bip.rl.ValueIterator;
import aub.edu.lb.kripke.Kripke;
import aub.edu.lb.model.Compound;
import ujf.verimag.bip.Core.Interactions.CompoundType;

public class TestRL1 {

	public static void main(String[] args) {
		CompoundType ct = TransformationFunction.parseBIPFile("bip-files/dining.bip");

		Compound compound = new Compound(ct);
		Kripke transitionSystem = new Kripke(compound);
		ValueIterator valueIterator = new ValueIterator(transitionSystem, "bench/badStates1");
		valueIterator.printDebug();

	}

}
