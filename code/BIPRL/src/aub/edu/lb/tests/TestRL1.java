package aub.edu.lb.tests;

import aub.edu.lb.bip.rl.ValueIterator;

public class TestRL1 {

	public static void main(String[] args) {
		ValueIterator valueIterator = new ValueIterator("bip-files/dining.bip", "bench/badStates1");
		valueIterator.printDebug();

	}

}
