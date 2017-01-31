package aub.edu.lb.kripke;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import aub.edu.lb.model.BIPInteraction;
import aub.edu.lb.model.GlobalState;
import aub.edu.lb.model.Compound;

public class Kripke {

	private GlobalState initialState;
	private KripkeState kripkeStateInitial;
	private Compound compound;

	// DFS
	private Stack<KripkeState> stack = new Stack<KripkeState>();
	protected HashSet<KripkeState> stateSpace = new HashSet<KripkeState>();
	protected List<Transition> transitions = new LinkedList<Transition>();
	protected Map<String, Integer> mapStateNameId = new HashMap<String, Integer>();
	protected Map<Integer, KripkeState> mapIdKripkeState = new HashMap<Integer, KripkeState>();

	private int counterState = 0; 
	/**
	 * 
	 * @param subSystem
	 */
	public Kripke(Compound compound) {
		this.compound = compound;
		initialState = compound.getInitialState();
		
		kripkeStateInitial = new KripkeState(initialState); 
		kripkeStateInitial.setId(counterState);
		mapStateNameId.put(kripkeStateInitial.getState().toString(), counterState);
		mapIdKripkeState.put(counterState, kripkeStateInitial);
		counterState++;

		stack.push(kripkeStateInitial);
		stateSpace.add(kripkeStateInitial);
		DFS();
		computeTransitions();
	}
	
	private void computeTransitions() {
		for(KripkeState state : getStates()) {
			for(Transition transition: state.getTransitions()) {
				transitions.add(transition);
			}
		}
	}
	
	public int getNumberStates() {
		return stateSpace.size();
	}

	public Kripke(Kripke kripke) {
		this.compound = kripke.compound;
		initialState = kripke.initialState;
		kripkeStateInitial = kripke.kripkeStateInitial;
		cloneStateSpace(kripke.stateSpace);
	}

	private void cloneStateSpace(HashSet<KripkeState> stateSpace) {
		this.stateSpace = new HashSet<KripkeState>(stateSpace.size());

		for (KripkeState state : stateSpace) {
			KripkeState copyState = new KripkeState(state);
			this.stateSpace.add(copyState);
			if (state == kripkeStateInitial)
				kripkeStateInitial = copyState;
		}

	}

	/**
	 * Depth-First Search
	 */
	private void DFS() {
		KripkeState kripkeState = stack.peek();
		GlobalState s = kripkeState.getState();
		List<BIPInteraction> enabledInteractions = compound.getEnabledInteractions(s);
		for (BIPInteraction interaction : enabledInteractions) {
			
			KripkeState kripkeStateNext = new KripkeState(compound.next(s, interaction));
			
			if (!stateSpace.contains(kripkeStateNext)) {
				Transition t = new Transition(kripkeState, kripkeStateNext, interaction);
				kripkeState.addTransition(t);
				stateSpace.add(kripkeStateNext);
				stack.push(kripkeStateNext);
				mapStateNameId.put(kripkeStateNext.getState().toString(), counterState);
				mapIdKripkeState.put(counterState, kripkeStateNext);
				kripkeStateNext.setId(counterState);
				counterState++;
				DFS();
			} else {
				kripkeStateNext = getKripkeState(kripkeStateNext);
				if (!kripkeState.getTransitions().contains(interaction)) {
					Transition t = new Transition(kripkeState, kripkeStateNext, interaction);
					kripkeState.addTransition(t);
				}
			}
		}
		stack.pop();
	}



	public HashSet<KripkeState> getStates() {
		return stateSpace;
	}
	
	public List<Transition> getTransitions() {
		return transitions;
	}


	public KripkeState getInitialState() {
		return kripkeStateInitial;
	}

	public Compound getCompound() {
		return compound;
	}

	
	public String toString() {
		String kripkeName = "[ ";
		for (KripkeState s : stateSpace)
			kripkeName += s + "  ";
		return kripkeName + "]";
	}

	public Integer getStateId(String name) {
		if(mapStateNameId.containsKey(name))
			return mapStateNameId.get(name);
		return null;
	}
	
	public KripkeState getState(int id) {
		return mapIdKripkeState.get(id); 
	}

	/**
	 * 
	 * @param kripkeState
	 *            -
	 * @return the kripkeState that "equals" to the kripkState given as input
	 */
	public KripkeState getKripkeState(KripkeState kripkeState) {
		for (KripkeState state : stateSpace) {
			if (state.equals(kripkeState))
				return state;
		}
		return null;
	}

}
