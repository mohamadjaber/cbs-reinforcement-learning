package aub.edu.lb.bip.expression;

import java.util.LinkedList;
import java.util.List;

public class TCompositeAction implements TAction {
	
	private List<TAction> actions; 
	
	public TCompositeAction() {
		actions = new LinkedList<TAction>();
	}
	
	public List<TAction> getContents() {
		return actions; 
	}
	
	public void addAction(TAction a) {
		actions.add(a);
	}
}
