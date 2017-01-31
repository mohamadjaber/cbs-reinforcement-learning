package aub.edu.lb.tests;

import aub.edu.lb.bip.rl.ValueIterator;

public class TestRL1 {

	public static void main1(String[] args) {
		ValueIterator valueIterator = new ValueIterator("bip-files/dining.bip", "bench/badStates1");
		valueIterator.compute();
		valueIterator.printDebug();
	}
	

	public static void main(String[] args) {
		ValueIterator valueIterator = new ValueIterator("/Users/jaber/Desktop/bench-tmp/d5g.bip", "/Users/jaber/Desktop/bench-tmp/badStatesd5g");
		valueIterator.compute();
		valueIterator.printDebug();
	}

}
