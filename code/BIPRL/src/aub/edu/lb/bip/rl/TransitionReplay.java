package aub.edu.lb.bip.rl;

import aub.edu.lb.model.GlobalState;
import aub.edu.lb.model.BIPInteraction;

public class TransitionReplay {
	private GlobalState fromState;
	private GlobalState toState;
	private BIPInteraction action; 
	private int reward; 
	
	public TransitionReplay(GlobalState fromState, GlobalState toState, BIPInteraction action, int reward) {
		this.fromState = fromState;
		this.toState = toState;
		this.action = action; 
		this.reward = reward;
	}
	
	public GlobalState getFromState() {
		return fromState;
	}
	
	public GlobalState getToState() {
		return toState;
	}
	
	public BIPInteraction getInteraction() {
		return action;
	}
	
	public int getReward() {
		return reward;
	}
	
}
