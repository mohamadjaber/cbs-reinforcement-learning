package aub.edu.lb.benchmarks;

public class TestGeneration {
	public static void main(String[] args) {
		generateRobots();
	}
	
	public static void generateRobots() {
		for(int i = 5; i < 30; i += 2) {
			new RobotGeneration(i, 2, "/home/mj54/Desktop/bench-forte/bench/robots/robots"+ i +"_2.bip", "/home/mj54/Desktop/bench-forte/bench/robots/robots"+ i +"_2.bad");
		}
	}
	
	public static void generateGoodDining() {
		for(int i = 2; i < 100; i += 5) {
			new DiningGoodGeneration(i, "/home/mj54/Desktop/bench-forte/bench/dining-good/dp" + i + ".bip", "/home/mj54/Desktop/bench-forte/bench/dining-good/dp" + i + ".bad");
		}
	}
	
	public static void generateBadDining() {
		for(int i = 2; i < 50; i += 5) {
			new DiningBadGeneration(i, "/home/mj54/Desktop/bench-forte/bench/dining-bad/dp" + i + ".bip", "/home/mj54/Desktop/bench-forte/bench/dining-bad/dp" + i + ".bad");
		}
	}
	
	
	public static void main1(String[] args) {
		new DiningGoodGeneration(5, "/Users/jaber/Desktop/bench-tmp/d5g.bip", "/Users/jaber/Desktop/bench-tmp/badStatesd5g");
		new RobotGeneration(20, 5, "/Users/jaber/Desktop/bench-tmp/robots3.bip", "/Users/jaber/Desktop/bench-tmp/badStatesrobots");
	}
}
