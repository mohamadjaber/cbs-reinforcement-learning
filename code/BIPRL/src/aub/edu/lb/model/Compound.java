package aub.edu.lb.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.PetriNet;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.Connector;

/**
 * 
 * @author Mohamad Jaber This class models the BIP System.
 */
public class Compound {

	protected GlobalState initialState;
	
	private List<LocalState> initialStates; 
	private CompoundType compoundType;
	private List<BIPInteraction> interactions;
	private List<Component> components;
	private long numberStates;

	/**
	 * 
	 * @param components
	 * @param interactions
	 */
	public Compound(CompoundType compoundType) {
		this.compoundType = compoundType;
		setInitialStates();
		setInteractions();
		setComponents();
		setInitialState();
	}
	
	public int stateLength() {
		return components.size();
	}

	/**
	 *
	 */
	private void setInitialState() {
		initialState = new GlobalState(getInitialStates(), this);
	}
	
	public long getNumberStates() {
		return numberStates; 
	}
	
	private void setComponents() {
		components = new ArrayList<Component>(compoundType.getSubcomponent().size());
		for(Component component: compoundType.getSubcomponent()) {
			components.add(component);
			numberStates *= getNumberStates(component);
		}
	}
	
	private void setInteractions() {
		interactions = new ArrayList<BIPInteraction>(compoundType.getConnector().size());
		for(Connector connector: compoundType.getConnector()) {
			BIPInteraction interaction = new BIPInteraction(connector);
			interactions.add(interaction);
		}
	}
	
	/**
	 * REQUIRES: comp is a component of type atomic
	 * @param comp 
	 * @return
	 */
	public long getNumberStates(Component comp) {
		AtomType atomicType = (AtomType) comp.getType();
		return ( (PetriNet) atomicType.getBehavior()).getState().size();
	}
	

	/**
	 * 
	 * @param compoundType
	 * @return
	 */
	private void setInitialStates() {
		initialStates = new ArrayList<LocalState>(compoundType.getSubcomponent().size());
		for(Component c : compoundType.getSubcomponent()) { 
			assert(c.getType() instanceof AtomType);
			PetriNet PN =  (PetriNet) ((AtomType)c.getType()).getBehavior();
			assert(PN.getInitialState().size() == 1); // behavior of atomic component is LTS
			initialStates.add(new LocalState(PN.getInitialState().get(0), c));
		}
	}
	
	public List<LocalState> getInitialStates() {
		return initialStates;
	}
	
	public List<BIPInteraction> getInteractions() {
		return interactions;
	}
	
	public CompoundType getCompoundType() {
		return compoundType;
	}
	
	public List<Component> getComponents() {
		return components;
	}
	

	public Component getComponent(String name) {
		for(Component component: components) {
			if(component.getName().equals(name))
				return component;
		}
		return null;
	}
	
	public List<BIPInteraction> getInteractions(Component component) {
		ArrayList<BIPInteraction> interactionsofComponent = new ArrayList<BIPInteraction>();
		for(BIPInteraction inter: interactions) {
			if(inter.getComponents().contains(component) && !interactionsofComponent.contains(inter))
				interactionsofComponent.add(inter);
		}
		return interactionsofComponent;
	}
	
	public List<BIPInteraction> getInteractionsContainsPort(String port) {
		List<BIPInteraction> interactions = new ArrayList<BIPInteraction>();
		for(BIPInteraction interaction: interactions) {
			if(interaction.getPorts().contains(port))
				interactions.add(interaction);
		}
		return interactions; 
	}
	
	/**
	 * TO VERIFY
	 */
	public List<BIPInteraction> getEnabledInteractions(GlobalState state) {
		LinkedList<BIPInteraction> enabledInteractions = new LinkedList<BIPInteraction>();
		for (BIPInteraction interaction : getInteractions()) {
			boolean isEnable = true;
			boolean leastOneParticipant = false;
			for (LocalState ls : state.getLocalStates()) {
				Component comp = ls.getComponent();
				if (interaction.getComponents().contains(comp)) {
					leastOneParticipant = true;
					String port = interaction.getPort(comp);
					if (ls.next(port) == null) {
						isEnable = false; // component comp does not ready the
											// interaction
						break;
					}
				}
			}
			if (isEnable && leastOneParticipant) {
				enabledInteractions.add(interaction);
			}
		}
		return enabledInteractions;
	}

	/**
	 * TO VERIFY
	 */
	public GlobalState next(GlobalState state, BIPInteraction interaction) {
		ArrayList<LocalState> nextLocalStates = new ArrayList<LocalState>();
		for (LocalState ls : state.getLocalStates()) {
			Component comp = ls.getComponent();
			String port = interaction.getPort(comp);
			LocalState nextLocalState = new LocalState(ls.next(port), comp);
			nextLocalStates.add(nextLocalState);
		}
		return new GlobalState(nextLocalStates, this);
	}
	

	/**
	 * 
	 * @return
	 */
	public GlobalState getInitialState() {
		return initialState;
	}

	public String toString() {
		String componentName = "Components: [ ";
		for (Component c : getComponents()) {
			componentName += c.getName() + " ";
		}
		componentName += "]";

		String initialStateName = "InitialState: " + initialState;

		String interactionName = "Interaction[s] : [ ";
		for (BIPInteraction interaction : getInteractions()) {
			interactionName += interaction + " ";
		}
		interactionName += "]";
		return "SubSystem:\n" + componentName + "\n" + interactionName + "\n" + initialStateName;
	}

}
