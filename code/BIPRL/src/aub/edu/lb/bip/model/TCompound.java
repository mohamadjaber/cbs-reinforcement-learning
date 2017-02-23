package aub.edu.lb.bip.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aub.edu.lb.bip.api.TransformationFunction;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import aub.edu.lb.bip.api.TEnumType;
import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TAction;
import aub.edu.lb.bip.expression.TCompositeAction;
import aub.edu.lb.bip.expression.TDoTogetherAction;
import aub.edu.lb.bip.expression.TNamedElement;
import aub.edu.lb.bip.expression.TVariable;
import aub.edu.lb.model.Compound;

public abstract class TCompound {
	protected Map<Component, TComponent> mapComponents;
	protected List<TComponent> tComponents;

	protected TInteractions tInteractions;
	protected TPriorities tPriorities;
	protected TVariable selector;
	protected TCompositeAction togetherAction;

	protected CompoundType compoundType;
	protected Compound compound;
	
	protected boolean withPriority;
	protected boolean optmized;

	protected boolean defaultInitializeVariables;

	protected String badStateFile; 
	
	/**
	 * @param compound
	 * @param optmized:
	 *            if true one cycle execution (port as wires, etc.), otherwise
	 *            two cycles.
	 * @param defaultInitilizeVariables:
	 *            if true initially, initialize all BIP variables to zero if
	 *            integers, or false if boolean. This will improve the
	 *            verification time.
	 */
	public TCompound(String bipFile, boolean optmized, boolean defaultInitializeVariables, String badStateFile) {
		this.badStateFile = badStateFile;
		compoundType = TransformationFunction.parseBIPFile(bipFile);
		compound = new Compound(compoundType);
		
		withPriority = compoundType.getPriorityRule().size() > 0;
		this.optmized = optmized;
		this.defaultInitializeVariables = defaultInitializeVariables;

		tInteractions = new TInteractions(this);

		selector = new TVariable(TogetherSyntax.selecter, TEnumType.WIRE_INT);
		tComponents = new ArrayList<TComponent>(compoundType.getSubcomponent().size());
		mapComponents = new HashMap<Component, TComponent>(compoundType.getSubcomponent().size());

		// Variables of the components use the interaction.
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = new TComponent(comp, this);
			mapComponents.put(comp, tComp);
			tComponents.add(tComp);
		}
	}
	
	public void compute() { }
	
	public Compound getCompound() {
		return compound;
	}

	protected void setTogetherAction() {
		togetherAction = new TCompositeAction();
		createVariables();
		createCurrentStates();
		createPorts();
		createInteractions();

		createStateEnum();

		if (defaultInitializeVariables)
			defaultInitializeVariables();

		initializeComponentsVariables();
		
		mainWhileLoopAction();	
	}
	
	protected void injectFooter(TCompositeAction action) {
		//FIXME RO REMOVE USED JUST FOR BENCHMARKS
		// action.getContents().add(new TNamedElement("cout << \":--> \" << r0_currentState << \" :--> \" << r1_currentState << endl;"));
		// action.getContents().add(new TNamedElement("cout << \":--> \" << r0_currentState << \" :--> \" << r1_currentState << \" :--> \" << r2_currentState << \" :--> \" << r3_currentState << endl;"));
		// action.getContents().add(new TNamedElement("cout << \":--> \" << r0_currentState << \" :--> \" << r1_currentState << \" :--> \" << r2_currentState << \" :--> \" << r3_currentState << \":--> \" << r4_currentState << \":--> \" << r5_currentState << \":--> \" << r6_currentState << \":--> \" << r7_currentState << endl;"));

		
		action.getContents().add(new TNamedElement(TogetherSyntax.counterVarName + " ++;")); 
		action.getContents().add(new TNamedElement("if(" + TogetherSyntax.counterVarName + " == " + TogetherSyntax.horizon + ") break;")); 

		action.getContents().add(new TNamedElement("bool deadlock = true;"));
		action.getContents().add(new TNamedElement("for(int i = 0; i < " + this.getTInteractions().size() + "; i++) {"));
		action.getContents().add(new TNamedElement(TogetherSyntax.tabSpace + "if(interactions_enablement[i]) {"));
		action.getContents().add(new TNamedElement(TogetherSyntax.tabSpace + TogetherSyntax.tabSpace +  "deadlock = false;"));
		action.getContents().add(new TNamedElement(TogetherSyntax.tabSpace + TogetherSyntax.tabSpace +  "cout<< \"selected\" << \" \"  << i << '\\n'; "));
		action.getContents().add(new TNamedElement(TogetherSyntax.tabSpace + "}"));
		action.getContents().add(new TNamedElement("}"));
		action.getContents().add(new TNamedElement("if(deadlock) {"));
		action.getContents().add(new TNamedElement(TogetherSyntax.tabSpace + "cout << \"deadlock\" << '\\n';"));
		action.getContents().add(new TNamedElement(TogetherSyntax.tabSpace + "break;"));
		action.getContents().add(new TNamedElement("}"));
	}
	


	public void setRandomSelector(TCompositeAction caCycle1) {
		TNamedElement action = new TNamedElement(TogetherSyntax.wire_prefix + TogetherSyntax.selecter + " = rand() % " +  this.getTInteractions().size() + ";");
		caCycle1.getContents().add(action);
	}
	
	
	private void defaultInitializeVariables() {
		TDoTogetherAction initialization = new TDoTogetherAction();
		TCompositeAction compositeAction = new TCompositeAction();
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			compositeAction.getContents().add(tComp.initializeVariables());
		}
		initialization.setAction(compositeAction);
		togetherAction.getContents().add(initialization);
	}

	public TComponent getTComponent(Component comp) {
		return mapComponents.get(comp);
	}

	public CompoundType getCompoundType() {
		return compoundType;
	}

	public TInteractions getTInteractions() {
		return tInteractions;
	}

	public TPriorities getTPriorities() {
		return tPriorities;
	}

	public TAction getTogetherAction() {
		return togetherAction;
	}

	public TVariable getSelecter() {
		return selector;
	}

	public boolean containsPriority() {
		return withPriority;
	}

	protected abstract void mainWhileLoopAction();

	protected void setInteractionEnablement(TCompositeAction ca) {
		setFirstInteractionEnablement(ca);
		if (withPriority) setFilterInteractionPriority(ca);
		setSelectOneInteraction(ca, withPriority);
	}
	


	protected void setSelectOneInteraction(TCompositeAction ca, boolean withPriority) {
		for (TInteraction tInteraction : tInteractions.getTInteractions()) {
			ca.getContents().add(tInteraction.getSelectOneInteraction(withPriority));
		}
	}

	protected void setFilterInteractionPriority(TCompositeAction ca) {
		for (TInteraction tInteraction : tInteractions.getTInteractions()) {
			ca.getContents().add(tInteraction.getFilterInteractionPriority());
		}
	}

	protected void setPortInteractionEnablement(TCompositeAction action) {
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			for (TPort tPort : tComp.getTPorts()) {
				action.getContents().add(tPort.getEnable().set(tPort.getEnable().getInteractionEnablement()));
			}
		}
	}

	protected void setFirstInteractionEnablement(TCompositeAction ca) {
		ca.getContents().add(getTInteractions().getFirstInteractionEnablement());
	}

	protected void setLocalPortEnablement(TCompositeAction action) {
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			for (TPort tPort : tComp.getTPorts()) {
				action.getContents().add(tPort.getLocalEnable().set(tPort.getLocalEnable().getEnablementExpression()));
			}
		}
	}

	protected void setNextStateFunctionLocationVariable(TCompositeAction action) {
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			action.getContents().add(tComp.nextStateFunctionLocationVariable());
		}
	}

	protected void setNextStateFunctionInteraction(TCompositeAction action) {
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			action.getContents().add(tComp.nextStateFunctionInteraction());
		}
	}

	protected void initializeComponentsVariables() {
		TDoTogetherAction tDoTogether = new TDoTogetherAction();
		TCompositeAction action = new TCompositeAction();
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			action.getContents().add(tComp.initialize());
		}

		tDoTogether.setAction(action);
		togetherAction.getContents().add(tDoTogether);
	}

	protected void createStateEnum() {
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			for (TState state : tComp.getTStates()) {
				togetherAction.getContents().add(state.create());
			}
		}
	}

	protected void createInteractions() {
		togetherAction.getContents().add(this.getTInteractions().create());
		togetherAction.getContents().add(this.getTInteractions().getTInteractionsFirstEnable().create());
		togetherAction.getContents().add(this.getTInteractions().getTInteractionsRuntimeEnforcement().create());
		if (withPriority)
			togetherAction.getContents().add(this.getTInteractions().getTInteractionsFilterPriority().create());
	}



	protected void createPorts() {
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			for (TPort tPort : tComp.getTPorts()) {
				togetherAction.getContents().add(tPort.getLocalEnable().create());
				togetherAction.getContents().add(tPort.getEnable().create());
			}
		}
	}

	protected void createCurrentStates() {
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			togetherAction.getContents().add(tComp.getCurrentState().create());
		}
	}

	protected void createVariables() {
		for (Component comp : compoundType.getSubcomponent()) {
			TComponent tComp = this.getTComponent(comp);
			togetherAction.getContents().add(tComp.createVariables());
			togetherAction.getContents().add(tComp.createInitializeDataParameter());

		}
		togetherAction.getContents().add(selector.create());
	}
}
