package aub.edu.lb.bip.api;


public class Helper {
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return random integer in [a, b[
	 */
	public static int random(int a, int b) {
		return a + (int) (Math.random() * (b - a));
	}
	

	/**
	 * PRE: numberElements < bound
	 * @param numberElements
	 * @param bound
	 * @return
	 */
	public static int[] generateRandomIndices(int numberElements, int bound) {
		int[] randoms = new int[numberElements];
		boolean[] found = new boolean[bound];
		int counter = 0; 
		
		while(counter < numberElements) {
			int rand = random(0, bound);
			if(!found[rand]) {
				found[rand] = true;
				randoms[counter++] = rand; 
			}
		}
		return randoms; 
	}


}
