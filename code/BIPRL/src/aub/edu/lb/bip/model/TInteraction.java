package aub.edu.lb.bip.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import aub.edu.lb.bip.api.Parser;
import aub.edu.lb.bip.api.TEnumType;
import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TArrayVariable;
import aub.edu.lb.bip.expression.TAssignmentAction;
import aub.edu.lb.bip.expression.TBinaryExpression;
import aub.edu.lb.bip.expression.TExpression;
import aub.edu.lb.bip.expression.TNamedElement;
import aub.edu.lb.bip.expression.TUnaryExpression;
import aub.edu.lb.bip.expression.TVariable;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryOperator;
import ujf.verimag.bip.Core.Behaviors.Port;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.Connector;
import ujf.verimag.bip.Core.Interactions.InnerPortReference;
import ujf.verimag.bip.Core.Interactions.InteractionSpecification;

public class TInteraction extends TVariable {
	public static int constInteractionID = 0;
	private Connector connector;
	private List<Component> components = new LinkedList<Component>();
	private Map<Component, Port> mapCompPort = new HashMap<Component, Port>();

	private TCompound tCompound;

	private int size;
	private final int id;

	public TInteraction(Connector conn, TCompound tCompound) {
		this.tCompound = tCompound;
		connector = conn;
		size = conn.getActualPort().size();
		for (Object o : conn.getActualPort()) {
			InnerPortReference ipr = (InnerPortReference) o;

			// No hierarchical connectors
			assert (ipr.getTargetInstance().getTargetPart() instanceof Component);

			Component comp = (Component) ipr.getTargetInstance().getTargetPart();
			Port p = ipr.getTargetPort();
			mapCompPort.put(comp, p);
			components.add(comp);
		}
		setName();
		setType();
		id = constInteractionID++;
	}

	public TExpression getExpressionReinforcementLearning() {
		TExpression expressionRL = new TNamedElement(TogetherSyntax.interactions_first_enable + "[" + getId() +"]");
		TInteractions tInteractions = tCompound.getTInteractions();
		for(TInteraction tNextInteraction : tInteractions.getTInteractions()) {
			if(!equals(tNextInteraction)) {
				int nextId = tNextInteraction.getId();
				TExpression left = new TUnaryExpression(UnaryOperator.LOGICAL_NOT, 
						new TNamedElement(TogetherSyntax.interactions_first_enable + "[" + nextId +"]"));
				
				TExpression right = new TBinaryExpression(
						BinaryOperator.GREATER_THAN_OR_EQUAL,
						new TNamedElement(TogetherSyntax.interactions_filtered_re + "[" + TogetherSyntax.current_state_identifier + " + \"_\" + to_string(" + getId() + ")]"),
						new TNamedElement(TogetherSyntax.interactions_filtered_re + "[" + TogetherSyntax.current_state_identifier + " + \"_\" + to_string(" + nextId + ")]")
					);
				
				
				TBinaryExpression binaryExpression = new TBinaryExpression(BinaryOperator.LOGICAL_OR,
						left, right);
				expressionRL = new TBinaryExpression(BinaryOperator.LOGICAL_AND, expressionRL, binaryExpression);
			}
		}
		return expressionRL;
	}
	
	public TExpression getExpressionDeepReinforcementLearning() {
		TExpression expressionRL = new TNamedElement(TogetherSyntax.interactions_first_enable + "[" + getId() +"]");
		TInteractions tInteractions = tCompound.getTInteractions();
		for(TInteraction tNextInteraction : tInteractions.getTInteractions()) {
			if(!equals(tNextInteraction)) {
				int nextId = tNextInteraction.getId();
				TExpression left = new TUnaryExpression(UnaryOperator.LOGICAL_NOT, 
						new TNamedElement(TogetherSyntax.interactions_first_enable + "[" + nextId +"]"));
				
				TExpression right = new TBinaryExpression(
						BinaryOperator.GREATER_THAN_OR_EQUAL,
						new TNamedElement(TogetherSyntax.interactions_filtered_re + "[" + getId() + "]"),
						new TNamedElement(TogetherSyntax.interactions_filtered_re + "[" + nextId + "]")
					);
				
				
				TBinaryExpression binaryExpression = new TBinaryExpression(BinaryOperator.LOGICAL_OR,
						left, right);
				expressionRL = new TBinaryExpression(BinaryOperator.LOGICAL_AND, expressionRL, binaryExpression);
			}
		}
		return expressionRL;
	}
	

	public TExpression getExpressionFairDeepReinforcementLearning(double fairnessDegreeDistance) {
		TExpression expressionRL = new TNamedElement(TogetherSyntax.interactions_first_enable + "[" + getId() +"]");
		TInteractions tInteractions = tCompound.getTInteractions();
		for(TInteraction tNextInteraction : tInteractions.getTInteractions()) {
			if(!equals(tNextInteraction)) {
				int nextId = tNextInteraction.getId();
				TExpression left = new TUnaryExpression(UnaryOperator.LOGICAL_NOT, 
						new TNamedElement(TogetherSyntax.interactions_first_enable + "[" + nextId +"]"));
				
				TNamedElement right1 = new TNamedElement("abs(" + TogetherSyntax.interactions_filtered_re + "[" + getId() + "]" +
						"-" + TogetherSyntax.interactions_filtered_re + "[" + nextId + "]) < " + fairnessDegreeDistance); 
						
				TExpression right2 = new TBinaryExpression(
						BinaryOperator.GREATER_THAN_OR_EQUAL,
						new TNamedElement(TogetherSyntax.interactions_filtered_re + "[" + getId() + "]"),
						new TNamedElement(TogetherSyntax.interactions_filtered_re + "[" + nextId + "]")
					);
				TBinaryExpression right = new TBinaryExpression(BinaryOperator.LOGICAL_OR, right1, right2);
				
				TBinaryExpression binaryExpression = new TBinaryExpression(BinaryOperator.LOGICAL_OR,
						left, right);
				expressionRL = new TBinaryExpression(BinaryOperator.LOGICAL_AND, expressionRL, binaryExpression);
			}
		}
		return expressionRL;
	}

	public TExpression getExpressionEnablement() {
		// FIXME -> add guard done but check.
		TExpression expressionEnablement = new TNamedElement(TogetherSyntax.true_condition);

		if (connector.getType().getInteractionSpecification().size() == 1) {
			InteractionSpecification interactionSpec = connector.getType().getInteractionSpecification().get(0);
			String guard = Parser.decompile(interactionSpec.getGuard(), true, null, connector, tCompound);
			if (!guard.equals(""))
				expressionEnablement = new TNamedElement(guard);
			else
				expressionEnablement = new TNamedElement(TogetherSyntax.true_condition);

		} else
			expressionEnablement = new TNamedElement(TogetherSyntax.true_condition);

		for (Component comp : mapCompPort.keySet()) {
			TComponent tComponent = tCompound.getTComponent(comp);
			TPort p = tComponent.getTPort(mapCompPort.get(comp));
			expressionEnablement = new TBinaryExpression(BinaryOperator.LOGICAL_AND, expressionEnablement,
					p.getLocalEnable());
		}
		return expressionEnablement;
	}

	/**
	 * FIXME - DONE interactions_filter_priority[id] =
	 * interaction_first_enable[id] /\ (\forall_{j \neq id}
	 * interactions_first_enable[j] => !priority[id][j]) priority variable can
	 * be remove. As priorities are static we can replace the (\forall_{j \neq
	 * id}) by for all j such that interactions[j] has more priority than id.
	 * E.g., interactions_filter_priority[0] = interaction_first_enable[0] /\
	 * (!interactions_first_enable[1] /\ !interactions_first_enable[7]) where
	 * interaction 1 and 7 have more priority than interaction 0.
	 */
	public TAssignmentAction getFilterInteractionPriority() {
		TInteractions tInteractions = tCompound.getTInteractions();
		TArrayVariable tInteractionFilterPriority = tInteractions.getTInteractionsFilterPriority();
		TArrayVariable tInteractionsFirstEnable = tInteractions.getTInteractionsFirstEnable();
		TArrayVariable assignedTarget = new TArrayVariable(tInteractionFilterPriority.getName(),
				tInteractionFilterPriority.getType(), new TNamedElement("" + this.id));
		TExpression expression = new TNamedElement(TogetherSyntax.true_condition);
		for (int j : TPriorities.morePriority(this)) {
			if (id != j) {
				TExpression notInteractionFirstEnable = new TUnaryExpression(UnaryOperator.LOGICAL_NOT,
						new TArrayVariable(tInteractionsFirstEnable.getName(), tInteractionsFirstEnable.getType(),
								new TNamedElement("" + j)));

				expression = new TBinaryExpression(BinaryOperator.LOGICAL_AND, expression, notInteractionFirstEnable);
			}
		}
		expression = new TBinaryExpression(BinaryOperator.LOGICAL_AND,
				new TArrayVariable(tInteractionsFirstEnable.getName(), tInteractionsFirstEnable.getType(),
						new TNamedElement("" + this.id)),
				expression);
		return assignedTarget.set(expression);
	}

	/**
	 * IF withpriority is equal to true THEN interactions_enablement[id] =
	 * interactions_filtered_priority[id] && (selecter == id || ( !
	 * interactions_filtered_priority[selecter] && \forall_{j > id}
	 * !interactions_filtered_priority[j])) Else interactions_enablement[id] =
	 * interactions_first_enable[id] && (selecter == id || ( !
	 * interactions_first_enable[selecter] && \forall_{j > id}
	 * !interactions_first_enable[j]))
	 */
	public TAssignmentAction getSelectOneInteraction(boolean withPriority) {
		TInteractions tInteractions = tCompound.getTInteractions();
		TArrayVariable tInteractionFilterPriority = null;
		if (withPriority) {
			tInteractionFilterPriority = tInteractions.getTInteractionsFilterPriority();
		} else {
			tInteractionFilterPriority = tInteractions.getTInteractionsFirstEnable();
		}
		TArrayVariable assignedTarget = new TArrayVariable(tInteractions.getName(), tInteractions.getType(),
				new TNamedElement("" + this.id));
		TExpression expression = new TNamedElement(TogetherSyntax.true_condition);
		for (int j = 0; j < tInteractions.size(); j++) {
			if (id < j) {
				TExpression notInteractionFilterPriority = new TUnaryExpression(UnaryOperator.LOGICAL_NOT,
						new TArrayVariable(tInteractionFilterPriority.getName(), tInteractionFilterPriority.getType(),
								new TNamedElement("" + j)));

				expression = new TBinaryExpression(BinaryOperator.LOGICAL_AND, expression,
						notInteractionFilterPriority);
			}
		}

		expression = new TBinaryExpression(BinaryOperator.LOGICAL_AND,
				new TUnaryExpression(UnaryOperator.LOGICAL_NOT, new TArrayVariable(tInteractionFilterPriority.getName(),
						tInteractionFilterPriority.getType(), tCompound.getSelecter())),
				expression);

		expression = new TBinaryExpression(BinaryOperator.LOGICAL_OR, new TBinaryExpression(BinaryOperator.EQUALITY,
				tCompound.getSelecter(), new TVariable("" + id, TEnumType.INT)), expression);

		expression = new TBinaryExpression(BinaryOperator.LOGICAL_AND,
				new TArrayVariable(tInteractionFilterPriority.getName(), tInteractionFilterPriority.getType(),
						new TNamedElement("" + this.id)),
				expression);
		return assignedTarget.set(expression);
	}

	private void setType() {
		type = TEnumType.BOOLEAN;
	}

	public String temporary() {
		return toString() + "_" + TogetherSyntax.temporary;
	}

	private void setName() {
		name = TogetherSyntax.interaction + "_" + connector.getName();
	}

	public int getId() {
		return id;
	}

	public int getSize() {
		return size;
	}

	public List<Component> getComponents() {
		return components;
	}

	public Port getPort(Component comp) {
		return mapCompPort.get(comp);
	}

	public Connector getConnector() {
		return connector;
	}

	public TCompound getTCompound() {
		return tCompound;
	}

}
