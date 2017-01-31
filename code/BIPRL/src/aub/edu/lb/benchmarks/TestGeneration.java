package aub.edu.lb.benchmarks;

public class TestGeneration {
	public static void main(String[] args) {
		new DiningGoodGeneration(5, "/Users/jaber/Desktop/bench-tmp/d5g.bip", "/Users/jaber/Desktop/bench-tmp/badStatesd5g");
		new DiningBadGeneration(3, "/Users/jaber/Desktop/bench-tmp/d3b.bip");
		new RobotGeneration(10, "/Users/jaber/Desktop/bench-tmp/robots3.bip");
	}
}
