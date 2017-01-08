package aub.edu.lb.model;

import java.util.ArrayList;
import java.util.List;

import ujf.verimag.bip.Core.Interactions.Component;

/**
 * 
 * @author Mohamad Jaber
 *
 */
public class GlobalState {
	private ArrayList<LocalState> localStates;
	private Compound subSystem;

	private int size;

	public GlobalState(List<LocalState> states, Compound subSystem) {
		size = states.size();
		localStates = new ArrayList<LocalState>(states);
		this.subSystem = subSystem;
	}

	public boolean readies(BIPInteraction interaction) {
		for (Component component : interaction.getComponents()) {
			if (!getLocalState(component).readies(interaction)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		return size;
	}

	/**
	 * 
	 * @return
	 */
	public Compound getSubSystem() {
		return subSystem;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<LocalState> getLocalStates() {
		return localStates;
	}

	public LocalState getLocalState(Component component) {
		for (LocalState localState : localStates) {
			if (localState.getComponent().equals(component))
				return localState;
		}
		return null;
	}

	/**
	 * @param gs
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GlobalState) {
			GlobalState gs = (GlobalState) obj;
			if (gs.size() != size)
				return false;
			return gs.localStates.containsAll(localStates);
		}
		return super.equals(obj);
	}

	/**
	 * 
	 */
	public int hashCode() {
		int hash = 0;
		for (LocalState ls : localStates) {
			hash += ls.hashCode() / size();
		}
		return hash;
	}
	
	public String getLocalIds() {
		String localIds = "";
		for (LocalState ls : localStates) {
			 localIds += ls.getId() + "_";
		}
		return localIds;
	}
	
	public double[] getIds() {
		double[] state = new double[localStates.size()];
		int i = 0; 
		for (LocalState ls : localStates) {
			 state[i++] = ls.getId();
		}
		return state;
	}

	public String toString() {
		if(localStates.size() == 0) return "";
		String globalStateName = localStates.get(0).toString();
		
		for (int i = 1; i < localStates.size(); i++) {
			globalStateName += "," + localStates.get(i).toString();
		}
		return globalStateName.trim();
	}

}
