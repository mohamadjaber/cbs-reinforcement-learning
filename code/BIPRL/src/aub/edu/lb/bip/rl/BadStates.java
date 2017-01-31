package aub.edu.lb.bip.rl;

import java.util.ArrayList;
import java.util.List;

import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.model.GlobalState;

public class BadStates {
	private List<BadState> badStates; 
	
	public BadStates() {
		badStates = new ArrayList<BadState>();
	}
	
	public void addBadState(BadState badState) {
		badStates.add(badState);
	}
	
	public boolean isBadState(KripkeState globalState) {
		for(BadState badState: badStates) {
			if(badState.isBadState(globalState)) return true;
		}
		return false;
	}
	
	public boolean isBadState(GlobalState globalState) {
		for(BadState badState: badStates) {
			if(badState.isBadState(globalState)) return true;
		}
		return false;
	}
}
