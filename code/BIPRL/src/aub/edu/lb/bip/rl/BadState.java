package aub.edu.lb.bip.rl;

import java.util.HashMap;
import java.util.Map;

import aub.edu.lb.kripke.KripkeState;
import aub.edu.lb.model.GlobalState;
import aub.edu.lb.model.LocalState;

/**
 * Symbolic bad state representation
 * TODO: add description
 * @author jaber
 *
 */
public class BadState {
	Map<String, String> componentLocations; 
	
	public BadState() {
		componentLocations = new HashMap<String, String>();
	}
	
	public BadState(String badState) {
		componentLocations = new HashMap<String, String>();
		String[] localStates = badState.replaceAll("\\s", "").split(",");
		for(String localState: localStates) {
			String[] compLoc = localState.split("\\.");
			addFieldState(compLoc[0], compLoc[1]);
		}
	}
	
	
	public void addFieldState(String component, String location) {
		if(!componentLocations.containsKey(component))
			componentLocations.put(component, location);
	}
	
	
	public boolean isBadState(KripkeState globalState) {
		for(LocalState localState: globalState.getState().getLocalStates()) {
			String comp = localState.getComponent().getName();
			String loc = localState.getState().getName();
			if(componentLocations.containsKey(comp) && !componentLocations.get(comp).equals(loc)) {
				return false; 
			}
		}
		return true;
	}
	
	public boolean isBadState(GlobalState globalState) {
		for(LocalState localState: globalState.getLocalStates()) {
			String comp = localState.getComponent().getName();
			String loc = localState.getState().getName();
			if(componentLocations.containsKey(comp) && !componentLocations.get(comp).equals(loc)) {
				return false; 
			}
		}
		return true;
	}
}
