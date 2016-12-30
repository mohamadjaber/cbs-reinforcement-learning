package aub.edu.lb.bip.rl;

import aub.edu.lb.model.GlobalState;
import aub.edu.lb.model.BIPInteraction;

public class TransitionReplay {
	private GlobalState fromState;
	private GlobalState toState;
	private BIPInteraction action; 
	private double reward; 
	
	public TransitionReplay(GlobalState fromState, BIPInteraction action, GlobalState toState, double reward) {
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
	
	public double getReward() {
		return reward;
	}
	
	@Override
	public String toString() {
		return fromState.toString() + " -- " + action.toString() + " --> " + toState.toString();
	}
	
}
