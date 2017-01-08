package aub.edu.lb.bip.api;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;



import ujf.verimag.bip.Core.ActionLanguage.Actions.ActionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Actions.AssignmentAction;
import ujf.verimag.bip.Core.ActionLanguage.Actions.CompositeAction;
import ujf.verimag.bip.Core.ActionLanguage.Actions.IfAction;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BinaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.BooleanLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.DataReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.ExpressionsFactory;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.FunctionCallExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.IntegerLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.RequiredDataParameterReference;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.StringLiteral;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryExpression;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.UnaryOperator;
import ujf.verimag.bip.Core.ActionLanguage.Expressions.VariableReference;
import ujf.verimag.bip.Core.Behaviors.Action;
import ujf.verimag.bip.Core.Behaviors.AtomType;
import ujf.verimag.bip.Core.Behaviors.BehaviorsFactory;
import ujf.verimag.bip.Core.Behaviors.Binding;
import ujf.verimag.bip.Core.Behaviors.ComponentType;
import ujf.verimag.bip.Core.Behaviors.DataParameter;
import ujf.verimag.bip.Core.Behaviors.DataType;
import ujf.verimag.bip.Core.Behaviors.DefinitionBinding;
import ujf.verimag.bip.Core.Behaviors.Expression;
import ujf.verimag.bip.Core.Behaviors.PetriNet;
import ujf.verimag.bip.Core.Behaviors.Port;
import ujf.verimag.bip.Core.Behaviors.PortDefinition;
import ujf.verimag.bip.Core.Behaviors.PortDefinitionReference;
import ujf.verimag.bip.Core.Behaviors.PortType;
import ujf.verimag.bip.Core.Behaviors.State;
import ujf.verimag.bip.Core.Behaviors.Transition;
import ujf.verimag.bip.Core.Behaviors.Variable;
import ujf.verimag.bip.Core.Interactions.Component;
import ujf.verimag.bip.Core.Interactions.CompoundType;
import ujf.verimag.bip.Core.Interactions.Connector;
import ujf.verimag.bip.Core.Interactions.ConnectorType;
import ujf.verimag.bip.Core.Interactions.ExportBinding;
import ujf.verimag.bip.Core.Interactions.InnerPortReference;
import ujf.verimag.bip.Core.Interactions.Interaction;
import ujf.verimag.bip.Core.Interactions.InteractionSpecification;
import ujf.verimag.bip.Core.Interactions.InteractionsFactory;
import ujf.verimag.bip.Core.Interactions.Part;
import ujf.verimag.bip.Core.Interactions.PartElementReference;
import ujf.verimag.bip.Core.Interactions.PortParameter;
import ujf.verimag.bip.Core.Interactions.PortParameterReference;
import ujf.verimag.bip.Core.Modules.Module;
import ujf.verimag.bip.Core.Modules.ModulesFactory;
import ujf.verimag.bip.Core.Modules.OpaqueElement;
import ujf.verimag.bip.Core.Modules.Root;
import ujf.verimag.bip.Core.PortExpressions.ACFusion;
import ujf.verimag.bip.Core.PortExpressions.ACTyping;
import ujf.verimag.bip.Core.PortExpressions.ACTypingKind;
import ujf.verimag.bip.Core.PortExpressions.PortExpression;
import ujf.verimag.bip.Core.PortExpressions.PortExpressionsFactory;
import ujf.verimag.bip.Core.PortExpressions.PortReference;
import ujf.verimag.bip.bip2src.Reverse;
import ujf.verimag.bip.parser.actions.Parser;


public class TransformationFunction {
	public static PortType PTSyn;
	public static ConnectorType ConnSyn;
	protected static BehaviorsFactory behavFactory = BehaviorsFactory.eINSTANCE ;
	protected static InteractionsFactory interFactory = InteractionsFactory.eINSTANCE ;
	protected static ActionsFactory actionFactory = ActionsFactory.eINSTANCE; 
	protected static ExpressionsFactory expressionFactory = ExpressionsFactory.eINSTANCE ; 
	protected static PortExpressionsFactory portexpressionFactory = PortExpressionsFactory.eINSTANCE ;
	protected static ModulesFactory moduleFactory = ModulesFactory.eINSTANCE ; 
	protected static Root top = null ;	

	public static void Initialize(Module m) {
		PTSyn = behavFactory.createPortType();
		setConnSynType();
		PTSyn.setName("SynEPort1");
		PTSyn.setModule(m);
		ConnSyn.setModule(m);	
	}
	
	
	/**
	 * REQUIRES: comp is a component of type atomic
	 * @param comp 
	 * @return
	 */
	public static long getNumberStates(Component comp) {
		AtomType atomicType = (AtomType) comp.getType();
		return ( (PetriNet) atomicType.getBehavior()).getState().size();
	}
	
	
	private static void setConnSynType() {
		ConnSyn = interFactory.createConnectorType();
		ConnSyn.setName("ConnSynPrint");
		PortParameter PortPara = interFactory.createPortParameter();
		PortPara.setType(PTSyn);
		PortPara.setName("p");
		ConnSyn.getPortParameter().add(PortPara);
		PortParameterReference PortParaRef = interFactory.createPortParameterReference();
		PortParaRef.setTarget(PortPara);
		ACFusion portdef = portexpressionFactory.createACFusion();;
		portdef.getOperand().add(PortParaRef);
		ConnSyn.setDefinition(portdef);
	}

	/**
	 * 
	 * @param v
	 * @param NewName
	 * @return
	 */
	public static Variable getCopyVariable(Variable v , String NewName) {
		Variable vcopy = (Variable) EcoreUtil.copy(v) ; 
		vcopy.setName(NewName) ; 
		return vcopy ; 
	}


	/**
	 * this method take as input a List of string and create boolean variables initialze to false
	 * Input  : val1 , val2
	 * Output : data bool val1 = false
	 * 			data bool val2 = false
	 */
	public static List<Variable> CreateGuardVariable (List<String> LName)
	{
		List<Variable> LVariable = new java.util.LinkedList<Variable>();
		for(Object name_i : LName)
		{
			String Name = (String) name_i;
			Variable BVariable = behavFactory.createVariable();
			OpaqueElement Oelem = moduleFactory.createOpaqueElement();
			BooleanLiteral BL = expressionFactory.createBooleanLiteral();
			BL.setBValue(false);
			Oelem.setBody("bool");
			BVariable.setType(Oelem);
			BVariable.setName(Name);
			BVariable.setInitialValue(BL);
			LVariable.add(BVariable);
		}
		return LVariable;
	}
	
	/**
	 * 
	 * @param v variable
	 * @return variable reference to v
	 */
	public static VariableReference CreateVariableReference(Variable v) {
		VariableReference vr = expressionFactory.createVariableReference() ; 
		vr.setTargetVariable(v) ; 
		return vr ; 
	}
	
	
	public static List<DataParameter> CreateDataParamter(List<Variable> LVariable)
	{
		int i = 0;
		List<DataParameter> LDataParameter = new java.util.LinkedList<DataParameter>();
		for(Object o : LVariable)
		{
			Variable v = (Variable) o;
			DataParameter dp = BehaviorsFactory.eINSTANCE.createDataParameter();
			dp.setType(v.getType());
			dp.setName("x" + i);
			i++;
			LDataParameter.add(dp);
		}
		return LDataParameter;
	}
	
	

	public static void SetTransitionInitialState(List<Transition> LTransition , State s1)
	{
		for(Object o : LTransition)
		{
			Transition t = (Transition) o ;
			t.getOrigin().clear();
			t.getOrigin().add(s1);
		}
	}
	
	/**
	 * 
	 * @param Name b1
	 * @param initialvalue false
	 * @return data bool b1 = true
	 */
	public static Variable CreateGuardVariable (String Name , boolean initialvalue)
	{
		Variable BVariable = behavFactory.createVariable();
		OpaqueElement Oelem = moduleFactory.createOpaqueElement();
		BooleanLiteral BL = expressionFactory.createBooleanLiteral();
		BL.setBValue(initialvalue);
		Oelem.setBody("bool");
		BVariable.setType(Oelem);
		BVariable.setName(Name);
		BVariable.setInitialValue(BL);
		return BVariable;
	}
	
	/**
	 * 
	 * @param var
	 * @param value
	 */
	public static void SetInitialValueBoolVar(Variable var , boolean value) {
		BooleanLiteral BL = expressionFactory.createBooleanLiteral();
		BL.setBValue(value);
		var.setInitialValue(BL) ;
	}
	
	
	
	/**
	 * 
	 * @param Name of the variable x
	 * @param initialvalue 0
	 * @return data int x = 0 
	 */
	public static Variable CreateIntVariable (String Name , int initialvalue)
	{
		Variable BVariable = behavFactory.createVariable();
		OpaqueElement Oelem = moduleFactory.createOpaqueElement();
		IntegerLiteral IL = expressionFactory.createIntegerLiteral();
		IL.setIValue(initialvalue) ;
		Oelem.setBody("int");
		BVariable.setType(Oelem);
		BVariable.setName(Name);
		BVariable.setInitialValue(IL);
		return BVariable;
	}
	
	/**
	 * 
	 * @param Name of the variable x
	 * @param initialvalue 0
	 * @return data const int x = 0 -> used for the translation from bip to abc
	 */
	public static Variable CreateConstIntVariable (String Name , int initialvalue) {
		Variable BVariable = behavFactory.createVariable();
		OpaqueElement Oelem = moduleFactory.createOpaqueElement();
		IntegerLiteral IL = expressionFactory.createIntegerLiteral();
		IL.setIValue(initialvalue) ;
		Oelem.setBody("const int");
		BVariable.setType(Oelem);
		BVariable.setName(Name);
		BVariable.setInitialValue(IL);
		return BVariable;
	}
	
	/**
	 * 
	 * @param Name of the variable x
	 * @param initialvalue "toto"
	 * @return data String x = "toto"
	 */
	public static Variable CreateStringVariable (String Name , String initialvalue)
	{
		Variable SVariable = behavFactory.createVariable();
		OpaqueElement Oelem = moduleFactory.createOpaqueElement();
		StringLiteral SL = expressionFactory.createStringLiteral() ; 
		SL.setSValue(initialvalue) ;
		Oelem.setBody("String");
		SVariable.setType(Oelem);
		SVariable.setName(Name);
		SVariable.setInitialValue(SL);
		return SVariable;
	}
	
	/**
	 * 
	 * @param VarName
	 * @param VarType
	 * @return
	 */
	public static Variable CreateVariable(String VarName , DataType VarType)
	{
		Variable Var = behavFactory.createVariable();
		Var.setName(VarName);
		Var.setType(VarType);
		return Var;
	}
	
	/**
	 * 
	 * @param LVarName
	 * @param LVariable
	 * @return
	 */
	public static List<Variable> CreateCopyVariables(List<String> LVarName , List<Variable> LVariable) {
		List<Variable> LOutputVariable  = new java.util.LinkedList<Variable>() ; 
		for(Object o : LVariable) {
			Variable v = (Variable) o ; 
			Variable Var = behavFactory.createVariable();
			Var.setName(LVarName.get(LVariable.indexOf(o))) ;
			Var.setType(v.getType()) ; 
			LOutputVariable.add(Var) ; 
		}
		return LOutputVariable ; 
		}


	/**
	 * this method take as input a list of data parameter (int x,int y) and a name and create the corresponding port type
	 * @param  : x,y  Int2Type
	 * @return : porttype Int2Type(int x, int y)
	 */
	public static PortType CreatePortType(List<DataParameter> LDP, String Name  , Module module)
	{
		PortType porttype = behavFactory.createPortType();
		for(DataParameter o : LDP) {
			porttype.getDataParameter().add(o);
		}
		porttype.setName(Name);
		porttype.setModule(module);
		return porttype;
	}
	

	
	/**
	 * this method take as input a list of variable x,y and a name and create the corresponding port type
	 * @param  : x,y  Int2Type
	 * @return : porttype Int2Type(int x, int y)
	 */
	public static PortType CreatePortTypeFromVar(List<Variable> LVariable, String Name  , Module module)
	{
		PortType porttype = behavFactory.createPortType();
		for(Object o : LVariable)
		{
			Variable var = (Variable) o ; 
			DataType dt = var.getType() ; 
			DataParameter dp = behavFactory.createDataParameter(); 
			dp.setName(var.getName()) ; 
			dp.setType(dt) ; 
			porttype.getDataParameter().add(dp);
		}
		porttype.setName(Name);
		porttype.setModule(module);
		return porttype;
	}


	
	public static Port CreatePort(String Name, PortType pt, AtomType at)
	{
		Port port = behavFactory.createPort();
		PortDefinition pd = CreatePortDefinition(Name,pt,at);
		DefinitionBinding db = behavFactory.createDefinitionBinding();
		db.setDefinition(pd);
		port.setBinding(db);
		port.setName(Name);
		port.setType(pt);
		port.setComponentType(at);
		at.getPort().add(port);
		return port;
	}
	
	public static Port CreatePort(String Name, PortType pt)
	{
		Port port = behavFactory.createPort();
		port.setName(Name);
		port.setType(pt);
		return port;
	}
	
	public static Port CreatePort(PortDefinition pd)
	{
		Port port = behavFactory.createPort();
		DefinitionBinding db = behavFactory.createDefinitionBinding();
		db.setDefinition(pd);
		port.setBinding(db);
		port.setName(pd.getName());
		port.setType(pd.getType());
		return port;
	}
	
	
	public static Port CreatePort(String Name, PortType pt, AtomType at, List<Variable> LVariable)
	{
		Port port = behavFactory.createPort();
		PortDefinition pd = CreatePortDefinition(Name,pt,at);
		pd.getExposedVariable().addAll(LVariable);
		DefinitionBinding db = behavFactory.createDefinitionBinding();
		db.setDefinition(pd);
		port.setBinding(db);
		port.setName(Name);
		port.setType(pt);
		port.setComponentType(at);
		at.getPort().add(port);
		return port;
	}
	
	public static Port CreatePort(String Name, PortType pt, List<Variable> LVariable)
	{
		Port port = behavFactory.createPort();
		PortDefinition pd = CreatePortDefinition(Name,pt);
		pd.getExposedVariable().addAll(LVariable);
		DefinitionBinding db = behavFactory.createDefinitionBinding();
		db.setDefinition(pd);
		port.setBinding(db);
		port.setName(Name);
		port.setType(pt);
		return port;
	}
	
	public static State getState(PetriNet PN, String StateName)
	{
		for(Object o : PN.getState())
		{
			State s = (State) o;
			if(s.getName().equals(StateName))
				return s;
		}
		return null;
	}
	
	/**
	 * this method take as input a list of variable and list of expressions and create the corresponding composition
	 * action describing bellow
	 * @input  : v1,v2,v3   x==0 && y < 7 , true , false
	 * @output : v1 = x==0 && y < 7 ; v2 = true ; v3 = false;
	 */
	public static CompositeAction CreateInitializeCAGuard(List<Variable> LVariable, List<Expression> LGuardExpression)
	{
		CompositeAction CompAction =  ActionsFactory.eINSTANCE.createCompositeAction();
		for(Object variable_i : LVariable)
		{
			Variable variable = (Variable) variable_i;
			AssignmentAction AssAction = ActionsFactory.eINSTANCE.createAssignmentAction();
			VariableReference VarRef = expressionFactory.createVariableReference();
			VarRef.setTargetVariable(variable);
			AssAction.setAssignedTarget(VarRef);
			int index = LVariable.indexOf(variable_i);
			AssAction.setAssignedValue(LGuardExpression.get(index));
			CompAction.getContent().add(AssAction);
		}
		return CompAction;
	}

	
	public static PortType CreatePortType(String Name)
	{
		PortType PT = behavFactory.createPortType();
		PT.setName(Name);
		return PT;
	}


	public static PortDefinition CreatePortDefinition(String Name, PortType PT,AtomType AT)
	{
		PortDefinition PD = behavFactory.createPortDefinition();
		PD.setAtomType(AT);
		PD.setType(PT);
		PD.setName(Name);
		AT.getPortDefinition().add(PD);
		return PD;
	}
	
	public static PortDefinition CreatePortDefinition(String Name, PortType PT)
	{
		PortDefinition PD = behavFactory.createPortDefinition();
		PD.setType(PT);
		PD.setName(Name);
		return PD;
	}

	public static PortDefinitionReference CreatePortDefinitionReference(PortDefinition PD)
	{
		PortDefinitionReference PDR = behavFactory.createPortDefinitionReference();
		PDR.setTarget(PD);
		return PDR;	
	}


	public static State CreateState(String Name, List<Transition> IN, List<Transition> OUT)
	{
		State S = behavFactory.createState();
		S.setName(Name);
		for(Transition o : IN)
		{
			S.getIncoming().add( o);
		}
		for(Transition o : OUT)
		{
			S.getOutgoing().add(o);
		}
		return S;
	}
	

	public
	static State CreateState(String Name , PetriNet PN)
	{
		State S = behavFactory.createState();
		S.setName(Name);
		PN.getState().add(S);
		return S;
	}


	public static Transition CreateTransition(PortDefinition PD, Expression guard, Action A, State s1 , State s2, PetriNet PN)
	{
		Transition t = behavFactory.createTransition();
		PortDefinitionReference PDR = behavFactory.createPortDefinitionReference();
		PDR.setTarget(PD);
		t.setTrigger(PDR);
		t.setAction(A);
		t.setGuard(guard);
		t.getOrigin().add(s1);
		t.getDestination().add(s2);
		PN.getTransition().add(t);
		s1.getOutgoing().add(t);
		s2.getIncoming().add(t);
		return t;
	}
	
	public static Transition getLTransition(PetriNet PN, State s1 , State s2) {
		for(Object o : PN.getTransition())
		{
			Transition t = (Transition) o;
			if(t.getOrigin().get(0).equals(s1) && t.getDestination().get(0).equals(s2))
				return t;
		}
		return null;
	}
	
	
	public static Transition CreateTransitionLState(PortDefinition PD,Expression guard,Action A, List<State> Ls1 , List<State> Ls2, PetriNet PN) {
		Transition t = behavFactory.createTransition();
		PortDefinitionReference PDR = behavFactory.createPortDefinitionReference();
		PDR.setTarget(PD);
		t.setTrigger(PDR);
		t.setAction(A);
		t.setGuard(guard);
		for(Object o : Ls1) {
			State s1 = (State) o;
			t.getOrigin().add(s1);
			s1.getOutgoing().add(t);
		}
		for(Object o : Ls2) {
			State s2 = (State) o;
			t.getDestination().add(s2);
			s2.getIncoming().add(t);
		}
		PN.getTransition().add(t);
		return t;
	}
	
	
	public static void RemoveGuardTransition(List<Transition> LTransition)
	{
		for(Object o : LTransition)
		{
			Transition t = (Transition) o;
			t.setGuard(null);
		}
	}
	
	
	public static void CopyTransitionFromState(List<Transition> LTransition , State s1 , State s2)
	{
		for(Object o : LTransition)
		{
			Transition t = (Transition) o ;
			t.getOrigin().clear();
			t.getOrigin().add(s2);
			s1.getOutgoing().remove(t);
			s2.getOutgoing().add(t);
		}
	}
	
	public static List<Connector> getConnectorfromPortComp(Port p, Component C, CompoundType CT)
	{
		List<Connector> LConn = new java.util.LinkedList<Connector>();
		for(Object o : CT.getConnector())
		{
			Connector conn = (Connector) o;
			for(Object o1: conn.getActualPort())
			{
				InnerPortReference IPR = (InnerPortReference)o1;
				if(IPR.getTargetPort().equals(p) && IPR.getTargetInstance().getTargetPart().equals(C))
				{
					LConn.add(conn);
					break;
				}
			}	
		}
		return LConn;
	}
	
	public static List<Variable> getCopyLVariable(List<Variable> LVariable)
	{
		List<Variable> LCopyVariable  = new java.util.LinkedList<Variable>();
		for(Object o : LVariable)
		{
			Variable v = (Variable) o;
			Variable vcopy = CreateVariable(v.getName(),v.getType());
			LCopyVariable.add(vcopy);
		}
		return LCopyVariable;
	}


	
	public static List<DataParameter> CreateDataParameter(List<Variable> LVariable)
	{
		int i = 0;
		List<DataParameter> LDataParameter = new java.util.LinkedList<DataParameter>();
		for(Object o : LVariable)
		{
			Variable v = (Variable) o;
			DataParameter dp = behavFactory.createDataParameter();
			dp.setType(v.getType());
			dp.setName("x" + i);
			i++;
			LDataParameter.add(dp);
		}
		return LDataParameter;
	}
	
	
	/**
	 * 
	 * @param LTransition list of transitions : t1 : from l to l1 port p1 guard g1 ,  t2 : from l to l2 port p2 guard g2 , 
	 * @param LPortName port p1 p2 p3
	 * @return g1 , g2 , false
	 */
	public static List<Expression> CorrespondingGuard(List<Transition> LTransition , List<String> LPortName)
	{
		List<Expression> LExpression = new java.util.LinkedList<Expression>();
		boolean exist ;
		for(Object o : LPortName)
		{
			String portname = (String) o;
			exist = false;
			for(Object o1 : LTransition)
			{
				Transition transition = (Transition) o1;
				PortDefinitionReference pdr = (PortDefinitionReference) transition.getTrigger();
				if(pdr.getTarget().getName().equals(portname))
				{
					exist = true;
					Expression guard = transition.getGuard();
					if(guard == null)
					{
						BooleanLiteral BL = expressionFactory.createBooleanLiteral();
						BL.setBValue(true);
						LExpression.add(BL);
					}
					else
					{
						LExpression.add(guard);
					}
				}
			}
			if(exist == false)
			{
				BooleanLiteral BL = expressionFactory.createBooleanLiteral();
				BL.setBValue(false);
				LExpression.add(BL);
			}
		}
		return LExpression;
	}
	
	public static List<AtomType> getAtomType(Module module)
	{
		List<AtomType> LAtomType = new java.util.LinkedList<AtomType>();
		for(Object o : module.getBipType())
		{
			if(o instanceof AtomType)
			{
				LAtomType.add((AtomType)o);
			}
		}
		return LAtomType;
	}
	

	
	static List<String> getLPortName(List<Port> LPort)
	{
		List<String> LPortName = new java.util.LinkedList<String>();
		for(Object o : LPort)
		{
			Port p = (Port) o;
			LPortName.add(p.getName());
		}
		return LPortName;
		
	}
	/**
	 * 
	 * @param LVariable
	 * @return
	 */
	public static List<String> getLVariableName(List<Variable> LVariable)
	{
		List<String> LVariableName = new java.util.LinkedList<String>();
		for(Object o : LVariable)
		{
			Variable v = (Variable) o;
			LVariableName.add(v.getName());
		}
		return LVariableName;	
	}
	

	/**
	 * 
	 * @param at  atom type
	 * @param LVariableName ['v1' 'v2' 'v3']
	 * @return [v1 v2 v3]
	 */
	public static List<Variable> getLVariable(AtomType at , List<String> LVariableName)
	{
		List<Variable> LVariable = new java.util.LinkedList<Variable>();
		for(Object o : at.getVariable()) {
			Variable v = (Variable) o;
			if(LVariableName.contains(v.getName()))
				LVariable.add(v) ; 
		}
		return LVariable;	
	}
	
	
	public static List<Variable> getVarPortEng(Port portCent, List<Variable> LVarCent, List<Variable> LVarDist)
	{
		List<Variable> LVariable = new java.util.LinkedList<Variable>();
		DefinitionBinding db = (DefinitionBinding) portCent.getBinding();
		List<Variable> LSetVar = db.getDefinition().getExposedVariable(); 
		for(Object o : LSetVar)
		{
			LVariable.add(LVarDist.get(LVarCent.indexOf(o)));
		}
		return LVariable;
	}
	

	
	/**
	 * 
	 * @param LVariable v1,v2,v3
	 * @return v1 && v2 && v3
	 */
	public static Expression CreateAndExpression(List<Variable> LVariable)
	{
		boolean  firststep = true;
		boolean  test = false;
		Expression BEtmp = expressionFactory.createBinaryExpression();
		BinaryExpression BE = null;
		for(Object o : LVariable)
		{
			Variable v = (Variable) o;
			VariableReference VarRef = expressionFactory.createVariableReference();
			VarRef.setTargetVariable(v);
			if(firststep == true)
			{
				BEtmp = VarRef;
				firststep = false;
			}
			else
			{
				test = true;
				BE = expressionFactory.createBinaryExpression();
				BE.setLeftOperand(BEtmp);
				BE.setRightOperand(VarRef);
				BinaryOperator BO = BinaryOperator.LOGICAL_AND;
				BE.setOperator(BO);
				BEtmp = BE;
			}
		}
		if (test == false) return BEtmp;
		else return BE;
	}
	
	
	/**
	 * 
	 * @param e1
	 * @param e2
	 * @return e1 && e2
	 */
	public static Expression AndOfTwoExpression(Expression e1 , Expression e2)
	{
		if(e2 != null)
		{
			BinaryExpression BE = expressionFactory.createBinaryExpression();
			BE.setLeftOperand((Expression)EcoreUtil.copy(e1));
			BE.setRightOperand((Expression)EcoreUtil.copy(e2));
			BinaryOperator BO = BinaryOperator.LOGICAL_AND;
			BE.setOperator(BO);
			return BE;
		}
		else return e1;
	}
	
	/**
	 * 
	 * @param e1
	 * @param e2
	 * @return e1 && e2
	 */
	public static Expression OROfTwoExpression(Expression e1 , Expression e2)
	{
		if(e2 != null)
		{
			BinaryExpression BE = expressionFactory.createBinaryExpression();
			BE.setLeftOperand((Expression)EcoreUtil.copy(e1));
			BE.setRightOperand((Expression)EcoreUtil.copy(e2));
			BinaryOperator BO = BinaryOperator.LOGICAL_OR ; 
			BE.setOperator(BO);
			return BE;
		}
		else return e1;
	}

	
	public static ConnectorType CreateConnectorTypeSend(PortType PT)
	{
		//ConnectorType ConnTransfer_PortType(PortType p1, PortType p2)
		ConnectorType ConnT = interFactory.createConnectorType();
		ConnT.setName("ConnTransferSR_" + PT.getName());
		PortParameter PortParaFrom = interFactory.createPortParameter();
		PortParaFrom.setType(PT);
		PortParaFrom.setName("p1");
		PortParameter PortParaTo = interFactory.createPortParameter();
		PortParaTo.setType(PT);
		PortParaTo.setName("p2");
		ConnT.getPortParameter().add(PortParaFrom);
		ConnT.getPortParameter().add(PortParaTo);
		PortParameterReference PortParaRefFrom = interFactory.createPortParameterReference();
		PortParaRefFrom.setTarget(PortParaFrom);
		PortParameterReference PortParaRefTo = interFactory.createPortParameterReference();
		PortParaRefTo.setTarget(PortParaTo);
		
		// [p1' p2]
		ACFusion portdef = portexpressionFactory.createACFusion();
		ACTyping prottrigfrom = portexpressionFactory.createACTyping();
		prottrigfrom.setOperand(PortParaRefFrom);
		prottrigfrom.setType(ACTypingKind.TRIG);
		portdef.getOperand().add(prottrigfrom);
		portdef.getOperand().add(PortParaRefTo);
		ConnT.setDefinition(portdef);
		
		// on p1 p2
		InteractionSpecification Ispec = interFactory.createInteractionSpecification();
		Interaction Inter = interFactory.createInteraction();
		Inter.getPort().add((PortReference) EcoreUtil.copy(PortParaRefFrom));
		Inter.getPort().add((PortReference) EcoreUtil.copy(PortParaRefTo));
		Ispec.setInteraction(Inter);
		ConnT.getInteractionSpecification().add(Ispec);
		
		// create down {p2.x = p1.x ; p2.y = p1.y; etc.}
		if(PT.getDataParameter().size() != 0)
		{
			CompositeAction CAD = ActionsFactory.eINSTANCE.createCompositeAction();
			CompositeAction CAU = ActionsFactory.eINSTANCE.createCompositeAction();
			for(Object o1 : PT.getDataParameter())
			{
				DataParameter DP = (DataParameter) o1;
				//p1.x
				RequiredDataParameterReference rdprfrom = expressionFactory.createRequiredDataParameterReference();
				PortParameterReference ppreffrom =  interFactory.createPortParameterReference();
				ppreffrom.setTarget(PortParaFrom);
				rdprfrom.setTargetParameter(DP);
				rdprfrom.setPortReference(ppreffrom);
				
				//p2.x
				RequiredDataParameterReference rdprto = expressionFactory.createRequiredDataParameterReference();
				PortParameterReference pprefto =  interFactory.createPortParameterReference();
				pprefto.setTarget(PortParaTo);
				rdprto.setTargetParameter(DP);
				rdprto.setPortReference(pprefto);
				
				//p2.x = p1.x
				AssignmentAction AAD = ActionsFactory.eINSTANCE.createAssignmentAction();
				AAD.setAssignedValue((Expression) EcoreUtil.copy(rdprfrom));
				AAD.setAssignedTarget((DataReference) EcoreUtil.copy(rdprto));
				CAD.getContent().add(AAD);
			}
			Ispec.setUpAction(CAU);
			Ispec.setDownAction(CAD);
		}
		
		// on p1 provided false
		InteractionSpecification Ispec1 = interFactory.createInteractionSpecification();
		Interaction Inter1 = interFactory.createInteraction();
		Inter1.getPort().add((PortReference) EcoreUtil.copy(PortParaRefFrom));
		Ispec1.setInteraction(Inter1);
		ConnT.getInteractionSpecification().add(Ispec1);
		BooleanLiteral bl = expressionFactory.createBooleanLiteral();
		bl.setBValue(false);
		Ispec1.setGuard(bl);
		
		PT.getModule().getBipType().add(ConnT);
		return ConnT;
	}
	
	
	/**
	 * 
	 * @param PT
	 * @return
	 */
	//[p1 p2] instead of [p1' p2] 
	
	public static ConnectorType CreateConnectorTypeSendReceive(PortType PT)
	{
		//ConnectorType ConnTransfer_PortType(PortType p1, PortType p2)
		ConnectorType ConnT = interFactory.createConnectorType();
		ConnT.setName("ConnTransfer_" + PT.getName());
		PortParameter PortParaFrom = interFactory.createPortParameter();
		PortParaFrom.setType(PT);
		PortParaFrom.setName("p1");
		PortParameter PortParaTo = interFactory.createPortParameter();
		PortParaTo.setType(PT);
		PortParaTo.setName("p2");
		ConnT.getPortParameter().add(PortParaFrom);
		ConnT.getPortParameter().add(PortParaTo);
		PortParameterReference PortParaRefFrom = interFactory.createPortParameterReference();
		PortParaRefFrom.setTarget(PortParaFrom);
		PortParameterReference PortParaRefTo = interFactory.createPortParameterReference();
		PortParaRefTo.setTarget(PortParaTo);
		
		// [p1 p2]
		ACFusion portdef = portexpressionFactory.createACFusion();
		portdef.getOperand().add(PortParaRefFrom);
		portdef.getOperand().add(PortParaRefTo);
		ConnT.setDefinition(portdef);
		
		// on p1 p2
		InteractionSpecification Ispec = interFactory.createInteractionSpecification();
		Interaction Inter = interFactory.createInteraction();
		Inter.getPort().add((PortReference) EcoreUtil.copy(PortParaRefFrom));
		Inter.getPort().add((PortReference) EcoreUtil.copy(PortParaRefTo));
		Ispec.setInteraction(Inter);
		ConnT.getInteractionSpecification().add(Ispec);
		
		// create down {p2.x = p1.x ; p2.y = p1.y; etc.}
		if(PT.getDataParameter().size() != 0)
		{
			CompositeAction CAD = ActionsFactory.eINSTANCE.createCompositeAction();
			CompositeAction CAU = ActionsFactory.eINSTANCE.createCompositeAction();
			for(Object o1 : PT.getDataParameter())
			{
				DataParameter DP = (DataParameter) o1;
				//p1.x
				RequiredDataParameterReference rdprfrom = expressionFactory.createRequiredDataParameterReference();
				PortParameterReference ppreffrom =  interFactory.createPortParameterReference();
				ppreffrom.setTarget(PortParaFrom);
				rdprfrom.setTargetParameter(DP);
				rdprfrom.setPortReference(ppreffrom);
				
				//p2.x
				RequiredDataParameterReference rdprto = expressionFactory.createRequiredDataParameterReference();
				PortParameterReference pprefto =  interFactory.createPortParameterReference();
				pprefto.setTarget(PortParaTo);
				rdprto.setTargetParameter(DP);
				rdprto.setPortReference(pprefto);
				
				//p2.x = p1.x
				AssignmentAction AAD = ActionsFactory.eINSTANCE.createAssignmentAction();
				AAD.setAssignedValue((Expression) EcoreUtil.copy(rdprfrom));
				AAD.setAssignedTarget((DataReference) EcoreUtil.copy(rdprto));
				CAD.getContent().add(AAD);
			}
			Ispec.setUpAction(CAU);
			Ispec.setDownAction(CAD);
		}
		PT.getModule().getBipType().add(ConnT);
		return ConnT;
	}
	
	
	
	
	
	/**
	 * 
	 * @param LPortType
	 * @param porttypexport
	 * @return
	 */
	
	public static ConnectorType CreateConnectorTypeMultipleSync(List<PortType> LPortType, PortType porttypexport)
	{
		//ConnectorType ConnTransfer_PortType(PortType p1, PortType p2 , ...)
		Map<PortType,PortParameterReference> Map_PT_PPR = new HashMap<PortType,PortParameterReference>() ;
		List<List<Variable>> LLVariable = new java.util.LinkedList<List<Variable>>() ; 
		List<Variable> LAllVariable = new java.util.LinkedList<Variable>() ; 
		ConnectorType ConnT = interFactory.createConnectorType();
		ConnT.setName("ConnTypeMultipleSync");
		int index = 0 ; 
		int index1 = 0 ; 

		ACFusion expressioninteraction = portexpressionFactory.createACFusion();
		for(Object o : LPortType) {
			
			PortType PT = (PortType) o ; 
			PortParameter PortPara = interFactory.createPortParameter();
			PortPara.setType(PT);
			PortPara.setName("p"+index++);
			ConnT.getPortParameter().add(PortPara);
			PortParameterReference PortParaRef = interFactory.createPortParameterReference();
			PortParaRef.setTarget(PortPara);
			Map_PT_PPR.put(PT, PortParaRef) ; 

			expressioninteraction.getOperand().add(PortParaRef);

			List<Variable> LVariable = new java.util.LinkedList<Variable>() ; 
			for(Object o2 : PT.getDataParameter()) {
				DataParameter DP = (DataParameter) o2 ; 
				Variable connectorVar = CreateVariable("var"+index1++ , DP.getType()) ; 
				ConnT.getVariable().add(connectorVar) ; 
				LVariable.add(connectorVar) ; 
				LAllVariable.add(connectorVar) ; 
			}
			LLVariable.add(LVariable) ; 
 		}
		ConnT.setDefinition(expressioninteraction);

		List<PortType> LPortTypeInteraction = LPortType; 
		InteractionSpecification Ispec = interFactory.createInteractionSpecification();
		Interaction Inter = interFactory.createInteraction();
		
		CompositeAction CAD = ActionsFactory.eINSTANCE.createCompositeAction();
		CompositeAction CAU = ActionsFactory.eINSTANCE.createCompositeAction();
		for(Object o1 : LPortTypeInteraction) {
			PortType porttype = (PortType) o1 ; 
			Inter.getPort().add((PortReference) EcoreUtil.copy(Map_PT_PPR.get(porttype)));
			Ispec.setInteraction(Inter);
			ConnT.getInteractionSpecification().add(Ispec);
			
			for(Object o2 : porttype.getDataParameter())
			{
				DataParameter DP = (DataParameter) o2;
				//p.x
				RequiredDataParameterReference rdpr = expressionFactory.createRequiredDataParameterReference();
				rdpr.setTargetParameter(DP);
				rdpr.setPortReference(getCopy(Map_PT_PPR.get(porttype)));
				
				//variable reference
				int indexport = LPortType.indexOf(porttype) ; 
				int indexdp = porttype.getDataParameter().indexOf(DP) ; 
				Variable var = LLVariable.get(indexport).get(indexdp) ;
				VariableReference varref = CreateVariableReference(var) ;

				//v = p.x
				AssignmentAction AAD = ActionsFactory.eINSTANCE.createAssignmentAction();
				AAD.setAssignedValue(rdpr);
				AAD.setAssignedTarget(varref);
				CAU.getContent().add(AAD);
			}
			Ispec.setUpAction(CAU);
			Ispec.setDownAction(CAD);
		}
	
		Port port = CreatePort("syncMonitor" , porttypexport , LAllVariable  ) ; 
		ConnT.setPortDefinition(getPortDefinition(port)) ; 
		ConnT.setPort(port) ; 
		return ConnT;
	}
	


	/**
	 * ConnectorType nameconntype (port p1 , port p2, port p3) // p2(a,b,c)
	 * @param p2.c
	 * @return : 2(p2),3(c)
	 */
	public static List<Integer> getIndexPortVar(ConnectorType ct , RequiredDataParameterReference rdpr)
	{
		List<Integer> LIndexPortVar = new java.util.LinkedList<Integer>();
		DataParameter dp = rdpr.getTargetParameter();
		PortParameter pp = rdpr.getPortReference().getTarget();
		for(Object o : ct.getPortParameter() )
		{
			PortParameter pptmp = (PortParameter) o;
			if(pptmp.equals(pp))
			{
				for(Object o1 : pp.getType().getDataParameter())
				{
					DataParameter dptmp = (DataParameter) o1;
					if(dptmp.equals(dp))
					{
						LIndexPortVar.add(ct.getPortParameter().indexOf(pp));
						LIndexPortVar.add(pp.getType().getDataParameter().indexOf(dp));
						return LIndexPortVar;
					}
				}
			}
		}
		return null;
	}

	
	
	
	public static Action CreateFucntionPrint(String s)
	{
		FunctionCallExpression FCE = expressionFactory.createFunctionCallExpression();
		StringLiteral SL = expressionFactory.createStringLiteral();
		SL.setSValue("\""+s+"\"");
		FCE.setFunctionName("printf");
		FCE.getActualData().add(SL);
		return FCE;
	}
	
	/**
	 * 
	 * @param FunctionName  f
	 * @param LVariable v1,v2,v3
	 * @return f(v1,v2,v3)
	 */
	
	public static FunctionCallExpression CreateFunctionCall(String FunctionName, List<Variable> LVariable)
	{
		FunctionCallExpression FCE = expressionFactory.createFunctionCallExpression();
		FCE.setFunctionName(FunctionName);
		for(Object o : LVariable)
		{
			Variable v = (Variable) o;
			VariableReference VR = expressionFactory.createVariableReference();
			VR.setTargetVariable(v);
			FCE.getActualData().add(VR);
		}
		return FCE;
	}
	
	/**
	 * 
	 * @param condition
	 * @param ifcase
	 * @param elsecase
	 * @return if(condition) { ifcase } else { elsecase }
	 */
	public static IfAction CreateIfAction(Expression condition , Action ifcase , Action elsecase) {
		IfAction ifaction = actionFactory.createIfAction() ; 
		ifaction.setCondition(condition) ; 
		ifaction.setIfCase(ifcase) ; 
		ifaction.setElseCase(elsecase) ; 
		return ifaction ; 

	}
	
	// get list of components for a connector
	public static List<Component> getLComponent(Connector c)
	{
		List<Component> LComponent = new java.util.LinkedList<Component>();
		for(Object o : c.getActualPort())
		{
			InnerPortReference ipr = (InnerPortReference) o;
			LComponent.add((Component)ipr.getTargetInstance().getTargetPart());
		}
		return LComponent;
	}
	
	
	
	/**
	 * This function take an Expression E = p.x + q.x + val1 ... and take list of port parameter p(x,y) q(x) ...  and for each port the corresponding list 
	 * list of variable [[v1 v2][v3]] so in this case it will replace p.x in the expression by
	 * v1, p.y by v2, q.x by v3 ....
	 */
	
	public static void ExpressionReplace ( Expression E, List<PortParameter> LPortParameter, List<List<Variable>>LLVariable)
	{
		if(E instanceof RequiredDataParameterReference)
		{
			RequiredDataParameterReference rdr = (RequiredDataParameterReference) E;
			int indexPortPara = LPortParameter.indexOf(rdr.getPortReference().getTarget());
			int indexVarPara = LPortParameter.get(indexPortPara).getType().getDataParameter().indexOf(rdr.getTargetParameter());
			
			VariableReference VarRef = expressionFactory.createVariableReference();
			Variable Var = LLVariable.get(indexPortPara).get(indexVarPara);
			VarRef.setTargetVariable(Var);
			EObject eo = E;
			if (eo.eContainer() instanceof FunctionCallExpression)
			{
				FunctionCallExpression fcall = (FunctionCallExpression) eo.eContainer();
				fcall.getActualData().set(fcall.getActualData().indexOf(E), VarRef);
			}
			else if (eo.eContainer() instanceof BinaryExpression)
			{
				BinaryExpression bexp = (BinaryExpression) eo.eContainer(); 
				if(bexp.getLeftOperand().equals(E))
					bexp.setLeftOperand(VarRef);
				if(bexp.getRightOperand().equals(E))
					bexp.setRightOperand(VarRef);
			}
			else if (eo.eContainer() instanceof AssignmentAction)
			{
				AssignmentAction aa = (AssignmentAction)eo.eContainer();
				if(aa.getAssignedTarget().equals(E))
					aa.setAssignedTarget(VarRef);
				if(aa.getAssignedValue().equals(E))
					aa.setAssignedValue(VarRef);
			}
			else if(eo.eContainer() instanceof UnaryExpression)
			{
				UnaryExpression UE = (UnaryExpression)eo.eContainer();
				UE.setOperand(VarRef);
			}
			else if(eo.eContainer() instanceof InteractionSpecification)
			{
				InteractionSpecification ispectmp = (InteractionSpecification)eo.eContainer();
				ispectmp.setGuard(VarRef);
			}
		}		
		else if(E instanceof FunctionCallExpression)
		{
			FunctionCallExpression Fcall = (FunctionCallExpression) E;
			for (Object o : Fcall.getActualData())
			{
				Expression E1 = (Expression) o;
				ExpressionReplace(E1, LPortParameter,LLVariable);
			}
		}
		else if(E instanceof BinaryExpression)
		{
			BinaryExpression BE = (BinaryExpression) E;	
			ExpressionReplace(BE.getRightOperand(), LPortParameter,LLVariable);
			ExpressionReplace(BE.getLeftOperand(), LPortParameter,LLVariable);
		}
		else if(E instanceof UnaryExpression)
		{
			UnaryExpression UE = (UnaryExpression)E;
			ExpressionReplace(UE.getOperand(), LPortParameter,LLVariable);
		}
		else if(E instanceof IfAction)
		{
			//TODO
		}			
	}
	
	
	

	/**
	 * This function take an Expression E = p.x + q.x + val1 ... and take list of port parameter p(x,y) q(x) ...  and for each port the corresponding list 
	 * list of variable [[v1 v2][v3]]. Furthermore, we give this function [varcon1 varcon2] and [varnewconn1 varnewconn2] 
	 * so in this case it will replace p.x in the expression by v1, p.y by v2, q.x by v3 and varcon2 by varnewconn1
	 */
	
	public static void ExpressionReplace ( Expression E, List<PortParameter> LPortParameter, List<List<Variable>>LLVariable , List<Variable> LVarBefore , List<Variable> LVarAfter)
	{
		if(E instanceof RequiredDataParameterReference)
		{
			RequiredDataParameterReference rdr = (RequiredDataParameterReference) E;
			int indexPortPara = LPortParameter.indexOf(rdr.getPortReference().getTarget());
			int indexVarPara = LPortParameter.get(indexPortPara).getType().getDataParameter().indexOf(rdr.getTargetParameter());
			
			VariableReference VarRef = expressionFactory.createVariableReference();
			Variable Var = LLVariable.get(indexPortPara).get(indexVarPara);
			VarRef.setTargetVariable(Var);
			EObject eo = E;
			if (eo.eContainer() instanceof FunctionCallExpression)
			{
				FunctionCallExpression fcall = (FunctionCallExpression) eo.eContainer();
				fcall.getActualData().set(fcall.getActualData().indexOf(E), VarRef);
			}
			else if (eo.eContainer() instanceof BinaryExpression)
			{
				BinaryExpression bexp = (BinaryExpression) eo.eContainer(); 
				if(bexp.getLeftOperand().equals(E))
					bexp.setLeftOperand(VarRef);
				if(bexp.getRightOperand().equals(E))
					bexp.setRightOperand(VarRef);
			}
			else if (eo.eContainer() instanceof AssignmentAction)
			{
				AssignmentAction aa = (AssignmentAction)eo.eContainer();
				if(aa.getAssignedTarget().equals(E))
					aa.setAssignedTarget(VarRef);
				if(aa.getAssignedValue().equals(E))
					aa.setAssignedValue(VarRef);
			}
			else if(eo.eContainer() instanceof UnaryExpression)
			{
				UnaryExpression UE = (UnaryExpression)eo.eContainer();
				UE.setOperand(VarRef);
			}
			else if(eo.eContainer() instanceof InteractionSpecification)
			{
				InteractionSpecification ispectmp = (InteractionSpecification)eo.eContainer();
				ispectmp.setGuard(VarRef);
			}
		}	
		else if (E instanceof VariableReference) {
			VariableReference varRef = (VariableReference)E ; 
			Variable var = varRef.getTargetVariable() ; 
			int indexOfVariable = LVarBefore.indexOf(var) ; 
			Variable varnew = LVarAfter.get(indexOfVariable) ; 
			varRef.setTargetVariable(varnew) ; 
		}
		else if(E instanceof FunctionCallExpression)
		{
			FunctionCallExpression Fcall = (FunctionCallExpression) E;
			for (Object o : Fcall.getActualData())
			{
				Expression E1 = (Expression) o;
				ExpressionReplace(E1, LPortParameter,LLVariable);
			}
		}
		else if(E instanceof BinaryExpression)
		{
			BinaryExpression BE = (BinaryExpression) E;	
			ExpressionReplace(BE.getRightOperand(), LPortParameter,LLVariable);
			ExpressionReplace(BE.getLeftOperand(), LPortParameter,LLVariable);
		}
		else if(E instanceof UnaryExpression)
		{
			UnaryExpression UE = (UnaryExpression)E;
			ExpressionReplace(UE.getOperand(), LPortParameter,LLVariable);
		}
		else if(E instanceof IfAction)
		{
			//TODO
		}			
	}
	
	
	public static Component CreateComponent(String name, ComponentType ct, CompoundType compoundType, @SuppressWarnings("rawtypes") List LParameter)
	{
		Component comp = interFactory.createComponent();
		comp.setName(name);
		comp.setType(ct);
		if(LParameter != null)
		{
			for(Object o : LParameter)
			{
				Expression parameter =(Expression) EcoreUtil.copy((EObject) o);
				comp.getActualData().add(parameter);
			}
		}
		compoundType.getSubcomponent().add(comp);
		return comp;
	}
	
	/**
	 * This method create a connector
	 * example input : conn , synchro, a b c, p1 p2 p3
	 * output connector syncro conn(a.p1,b.p2,c.p3)
	 */
	
	public static Connector CreateConnector(String name, ConnectorType connType, CompoundType compoundType,List<Component> LComponent , List<Port> LPort)
	{
		Connector connector = interFactory.createConnector();
		connector.setName(name);
		connector.setType(connType);
		connector.setCompoundType(compoundType);
		
		for(Object o : LComponent)
		{
			Component comp = (Component) o ;
			Port p = LPort.get(LComponent.indexOf(o));
			InnerPortReference ipr = interFactory.createInnerPortReference();
			ipr.setTargetPort(p);
			PartElementReference PE = interFactory.createPartElementReference();
			PE.setTargetPart(comp);
			ipr.setTargetInstance(PE);
			connector.getActualPort().add(ipr);
		}
		return connector;
	}
	
	/**
	 * This method create a connector
	 * example input : conn , synchro, a b c, p1 p2 p3
	 * output connector syncro conn(a.p1,b.p2,c.p3)
	 */
	
	public static Connector createConnector(String name, ConnectorType connType, CompoundType compoundType,List<Part> LComponent , List<Port> LPort)
	{
		Connector connector = interFactory.createConnector();
		connector.setName(name);
		connector.setType(connType);
		connector.setCompoundType(compoundType);
		
		for(Object o : LComponent)
		{
			Port p = LPort.get(LComponent.indexOf(o));
			InnerPortReference ipr = interFactory.createInnerPortReference();
			ipr.setTargetPort(p);
			PartElementReference PE = interFactory.createPartElementReference();
			PE.setTargetPart((Part)o);
			ipr.setTargetInstance(PE);
			connector.getActualPort().add(ipr);
		}
		return connector;
	}
	
	/**
	 * this method create export binding port in the compound component
	 * @input p,A,p1,compoundType
	 * @return export port Typeofp1 p is A.p1   (in the compoundType)
	 */
	
	public static Port CreatePort(String name, Component component, Port p1,CompoundType compoundType)
	{
		Port p = behavFactory.createPort();
		ExportBinding eb = interFactory.createExportBinding();
		PartElementReference PER = interFactory.createPartElementReference();	
		PER.setTargetPart(component);
		eb.setTargetInstance(PER);
		eb.setTargetPort(p1);
		p.setBinding(eb);
		p.setName(name);
		p.setType(p1.getType());
		p.setComponentType(compoundType);
		compoundType.getPort().add(p);
		return p;
	}
	
	
	/**
	 * Create Interaction specification on p1 p2 provided true up {} down {} by default
	 */
	
	public static InteractionSpecification CreateInteractionSpecification(ConnectorType connType)
	{
		InteractionSpecification intspec = interFactory.createInteractionSpecification();
		Interaction inter = interFactory.createInteraction();
		intspec.setInteraction(inter);
		for(Object o : connType.getPortParameter())
		{
			PortParameter pp = (PortParameter) o;
			PortParameterReference PortParaRef = interFactory.createPortParameterReference();
			PortParaRef.setTarget(pp);
			inter.getPort().add(PortParaRef);
		}
		intspec.setDownAction(actionFactory.createCompositeAction()) ;
		intspec.setUpAction(actionFactory.createCompositeAction()) ;
		return intspec;
	}


	/**
	 *  create a copy of atom type
	 */
	public static AtomType getCopy(AtomType at) {
		AtomType copyat = behavFactory.createAtomType();
		copyat = (AtomType) EcoreUtil.copy(at);	
		return copyat;
	}
	
	/**
	 *  create a copy of atom type
	 */
	public static PortParameterReference getCopy(PortParameterReference ppr) {
		PortParameterReference pprcopty = interFactory.createPortParameterReference();
		pprcopty = (PortParameterReference) EcoreUtil.copy(ppr);	
		return pprcopty;
	}

	
	/**
	 *  create a copy of atom type
	 */
	public static AtomType CreateAtomType(String Name) {
		AtomType atomtype = behavFactory.createAtomType();
		atomtype.setName(Name) ;
		atomtype.setBehavior(behavFactory.createPetriNet()) ; 
		return atomtype;
	}
	
	
	/**
	 *  create a copy of component 
	 */
	public static Component getCopy(Component component) {
		Component componentcopy = interFactory.createComponent();
		componentcopy = (Component) EcoreUtil.copy(component);	
		return componentcopy;
	}
	
	
	/**
	 *  create a copy of atom type
	 */
	public static Expression getCopy(Expression exp) {
		Expression expcopy = (Expression) EcoreUtil.copy(exp);	
		return expcopy;
	}
	
	/**
	 *  create a copy of compound type type
	 */
	public static CompoundType getCopy(CompoundType compoundType) {
		CompoundType compoundTypecopy = (CompoundType) EcoreUtil.copy(compoundType);	
		return compoundTypecopy;
	}
	
	/**
	 *  create a copy of compound type type
	 */
	public static Binding getCopy(Binding binding) {
		Binding bindingcopy = (Binding) EcoreUtil.copy(binding);	
		return bindingcopy;
	}
	
	/**
	 *  create a copy of action
	 */
	public static Action getCopy(AssignmentAction action) {
		Action actioncopy = (Action) EcoreUtil.copy(action);	
		return actioncopy;
	}
	
	/**
	 * create a copy of port 
	 * @param port : export port IntPort p1(x,y)
	 * @param NewName p1new
	 * @return  export port IntPort p2(x,y)
	 */
	public static Port getCopy(Port port , String NewName) {
		Port copyport = behavFactory.createPort();
		PortDefinition pd = ((DefinitionBinding)port.getBinding()).getDefinition() ; 
		PortDefinition copyportdefinition =  getCopy(pd);
		copyportdefinition.setName(NewName) ; 
		copyport = (Port) EcoreUtil.copy(port);	
		((DefinitionBinding)copyport.getBinding()).setDefinition(copyportdefinition) ; 
		copyport.setName(NewName) ; 
		return copyport;
	}
	
	// create a copy of port Definition
	public static PortDefinition getCopy(PortDefinition portdefinition) {
		PortDefinition copyportdefinition = behavFactory.createPortDefinition();
		copyportdefinition = (PortDefinition) EcoreUtil.copy(portdefinition);	
		return copyportdefinition;
	}
	
	// create a copy of data parameter
	public static DataParameter getCopy(DataParameter dataparameter) {
		DataParameter copydataparameter = (DataParameter) EcoreUtil.copy(dataparameter);	
		return copydataparameter;
	}
	
	
	/**
	 * @param p1 port type p1(int x , int y )
	 * @param p2 port type(int z , int t)
	 * @return if p1 and p2 has the same port type in this case yes it return true else it return false
	 * for example (int , char)  (int , int ) it returns false
	 */
	public boolean IsEquals(PortType p1 , PortType p2)
	{
		for(Object o : p1.getDataParameter())
		{
			DataParameter dp = (DataParameter) o ; 
			if(dp.getType() instanceof OpaqueElement)
			{
				OpaqueElement oe = (OpaqueElement) dp.getType() ; 
				int indexOfDataParameter = p1.getDataParameter().indexOf(o) ; 
				DataParameter dp1 = (DataParameter) p2.getDataParameter().get(indexOfDataParameter) ; 
				if(dp1.getType() instanceof OpaqueElement)
				{
					OpaqueElement oe1 = (OpaqueElement) dp1.getType() ;
					if(!oe.getBody().equals(oe1.getBody()))
						return false ; 
				}
				else 
					return false ; 
			}
			else
				return false ; 
		}
		return true;
	}
	
	/**
	 * @param s input state 
	 * @param LNameExportPort list of the name of the export port in the atomic component
	 * @return the list of the transition labeled by the export ports
	 */
	public static List<Transition> getTransitionExpPort(State s , List<String>  LNameExportPort)
	{
		List<Transition> LTransition = new java.util.LinkedList<Transition>();
		for(Object o :s.getOutgoing())
		{
			Transition t = (Transition) o;
			PortDefinitionReference pdr = (PortDefinitionReference) t.getTrigger();
			if(LNameExportPort.indexOf(pdr.getTarget().getName()) != -1)
				LTransition.add(t);
		}
		return LTransition;
	}
	
	public static List<Transition> getTransitionIntPort(State s , List<String>  LNameExportPort)
	{
		List<Transition> LTransition = new java.util.LinkedList<Transition>();
		for(Object o :s.getOutgoing())
		{
			Transition t = (Transition) o;
			PortDefinitionReference pdr = (PortDefinitionReference) t.getTrigger();
			if(LNameExportPort.indexOf(pdr.getTarget().getName()) == -1)
				LTransition.add(t);
		}
		return LTransition;
	}
	
	/**
	 * this method take as input a state and return a list of string of the possible port (export) from this state
	 */
	public static List<String> getOutStringExpPort(State s , List<String> LNameExportPort)
	{
		List<String> SubLStringPort = new java.util.LinkedList<String>();
		for(Object transition_i : s.getOutgoing())
		{
			Transition transition = (Transition) transition_i;
			PortDefinitionReference PDR = (PortDefinitionReference) transition.getTrigger();
			String NameofPDR = PDR.getTarget().getName();
			if(LNameExportPort.contains(NameofPDR))
				SubLStringPort.add(NameofPDR);
		}
		return SubLStringPort;
	}
	
	/**
	 * 
	 * @param v input variable for example v1 
	 * @return expression not of the input variable !v
	 */
	public static Expression getInverseGuard(Variable v)
	{
		UnaryExpression NotE = expressionFactory.createUnaryExpression();
		UnaryOperator Not = UnaryOperator.LOGICAL_NOT ; 
		VariableReference VarRef = expressionFactory.createVariableReference();
		VarRef.setTargetVariable(v);
		NotE.setOperator(Not) ; 
		NotE.setOperand(VarRef)  ; 
		return NotE ; 
	}
	
	/**
	 * 
	 * @param v 
	 * @return &v 
	 */
	public static UnaryExpression CreateUnaryExpressionReference(Variable v) {
		UnaryExpression UE = expressionFactory.createUnaryExpression();
		UnaryOperator RefOP = UnaryOperator.REFERENCE; 
		VariableReference VarRef = expressionFactory.createVariableReference();
		VarRef.setTargetVariable(v);
		UE.setOperand(VarRef) ; 
		UE.setOperator(RefOP) ; 
		return UE ; 
	}

	
	
	
	
	/**
	 * 
	 * @param LVariable v1,v2,v3
	 * @return v1 || v2 || v3
	 */
	public static Expression getOrExpression(List<Variable> LVariable)
	{
		boolean  firststep = true;
		boolean  test = false;
		Expression BEtmp = expressionFactory.createBinaryExpression();
		BinaryExpression BE = null;
		for(Object o : LVariable)
		{
			Variable v = (Variable) o ; 
			VariableReference VarRef = expressionFactory.createVariableReference();
			VarRef.setTargetVariable(v);
			if(firststep == true)
			{
				BEtmp = VarRef;
				firststep = false;
			}
			else
			{
				test = true;
				BE = expressionFactory.createBinaryExpression();
				BE.setLeftOperand(BEtmp);
				BE.setRightOperand(VarRef);
				BinaryOperator BO = BinaryOperator.LOGICAL_OR;
				BE.setOperator(BO);
				BEtmp = BE;
			}
		}
		if (test == false) return BEtmp;
		else return BE;
	}
		
	

	/**
	 * 
	 * @param connector input a connector
	 * @param LPortParameter p(x,y)  q(x)
	 * @param LLVariable  [[v1 v2] [v3] ]
	 * @return the guard of the connector by replacing p.x by v1 and p.y by v2 and q.x by v3
	 */
	
	public static  Expression getNewGuardConnector(ConnectorType connType , List<PortParameter> LPortParameter , List<List<Variable>> LLVariable) {
 		List<InteractionSpecification> LIS =connType.getInteractionSpecification();
		if(LIS.size()!= 0) {
			InteractionSpecification  IS = LIS.get(0);
			Expression guardbefore = IS.getGuard();
			if(guardbefore != null) {
				Expression guardafter = (Expression) EcoreUtil.copy(guardbefore);
				TransformationFunction.ExpressionReplace(guardafter, LPortParameter, LLVariable);
				return guardafter ; 
			}
		}
		return null;
	}
	

	/**
	 * 
	 * @param connector input a connector
	 * @param LPortParameter p(x,y)  q(z)
	 * @param LLVariable [[v1 v2][v3]]
	 * @param LVariableConnBefore [val1 val2 val3]
	 * @param LVariableConnAfter [newval1 newval2 newval3]
	 * @return the up();down(); by replacing p.x by v1 and p.y by v2 and q.z by v3 and val1 by newval1 , val2 by newval2 , val3 by newval3
	 */
	
	public static Action getNewFunctionConnector(ConnectorType connType , List<PortParameter> LPortParameter , List<List<Variable>> LLVariable ,
											List<Variable> LVariableConnBefore , List<Variable> LVariableConnAfter) {
		if(connType.getInteractionSpecification().size() != 0) {
			
			InteractionSpecification IS = (InteractionSpecification)connType.getInteractionSpecification().get(0) ; 
			CompositeAction CompAction = actionFactory.createCompositeAction();
			
			//Replace UP Action
			if(IS.getUpAction() instanceof CompositeAction) {
				CompositeAction CA = (CompositeAction)IS.getUpAction() ; 
				for(Object o : CA.getContent()) {
					if(o instanceof AssignmentAction) {
						AssignmentAction AA = (AssignmentAction) o ; 
						AssignmentAction aacopy = (AssignmentAction) EcoreUtil.copy(AA); 
						TransformationFunction.ExpressionReplace(aacopy.getAssignedValue(), LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						TransformationFunction.ExpressionReplace(aacopy.getAssignedTarget(), LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						CompAction.getContent().add(aacopy);
					}
					else if(o instanceof FunctionCallExpression) {
						FunctionCallExpression fce = (FunctionCallExpression) o;
						FunctionCallExpression fcecopy = (FunctionCallExpression) EcoreUtil.copy(fce); 
						TransformationFunction.ExpressionReplace(fcecopy,LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						CompAction.getContent().add(fcecopy);
					}
					else if(o instanceof IfAction) {
						IfAction ifaction = (IfAction) o ; 
						IfAction ifactioncopy = (IfAction) EcoreUtil.copy(ifaction); 
						TransformationFunction.ExpressionReplace(ifactioncopy.getCondition(),LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						TransformationFunction.ExpressionReplace((Expression)ifactioncopy.getIfCase(),LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						TransformationFunction.ExpressionReplace((Expression)ifactioncopy.getElseCase(),LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						CompAction.getContent().add(ifactioncopy);
					}
					else {
						System.out.println("Problem up action in the connector type:  " + connType.getName());
					}
					
				}
			}
			
			//Replace Down Action
			if(IS.getDownAction() instanceof CompositeAction) {
				CompositeAction CA = (CompositeAction)IS.getDownAction() ; 
				for(Object o : CA.getContent()) {
					if(o instanceof AssignmentAction) {
						AssignmentAction AA = (AssignmentAction) o ; 
						AssignmentAction aacopy = (AssignmentAction) EcoreUtil.copy(AA); 
						TransformationFunction.ExpressionReplace(aacopy.getAssignedValue(), LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						TransformationFunction.ExpressionReplace(aacopy.getAssignedTarget(), LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						CompAction.getContent().add(aacopy);
					}
					else if(o instanceof FunctionCallExpression) {
						FunctionCallExpression fce = (FunctionCallExpression) o;
						FunctionCallExpression fcecopy = (FunctionCallExpression) EcoreUtil.copy(fce); 
						TransformationFunction.ExpressionReplace(fcecopy,LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						CompAction.getContent().add(fcecopy);
					}
					else if(o instanceof IfAction) {
						IfAction ifaction = (IfAction) o ; 
						IfAction ifactioncopy = (IfAction) EcoreUtil.copy(ifaction); 
						TransformationFunction.ExpressionReplace(ifactioncopy.getCondition(),LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						TransformationFunction.ExpressionReplace((Expression)ifactioncopy.getIfCase(),LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						TransformationFunction.ExpressionReplace((Expression)ifactioncopy.getElseCase(),LPortParameter, LLVariable , LVariableConnBefore , LVariableConnAfter);
						CompAction.getContent().add(ifactioncopy);
					}
					else {
						System.out.println("Problem down action in the connector type:  " + connType.getName());
					}
					
				}
			}
			return CompAction; 	
		}
		return null;
	}
	
	/**
	 * 
	 * @param port
	 * @return
	 */
	public static PortDefinition getPortDefinition(Port port) {
		if(port.getBinding() instanceof DefinitionBinding) {
			DefinitionBinding db = (DefinitionBinding) port.getBinding() ; 
			return db.getDefinition()  ; 
		}
		System.out.println("Problem in the port port" + port.getName()) ; 
		System.out.println("Debug : TransformationFunction getPortDefinition Method") ; 
		return null ; 	
	}
	
	public static PortDefinitionReference createPortDefinitionReference(Port port)
	{
		assert(port.getBinding() instanceof DefinitionBinding): "error while create a new port definition reference";
		DefinitionBinding db = (DefinitionBinding) port.getBinding() ; 
		PortDefinitionReference pdr = behavFactory.createPortDefinitionReference();
		pdr.setTarget(db.getDefinition());
		return pdr; 
	}
	
	
	/**
	 * 
	 * @param var v1
	 * @param value  true
	 * @return Action    v1 := true
	 */
	public static AssignmentAction CreateAssignmentActionBoolVar(Variable var , boolean value) {
		AssignmentAction AA = actionFactory.createAssignmentAction() ; 
		VariableReference VarRef = expressionFactory.createVariableReference() ; 
		BooleanLiteral BL = expressionFactory.createBooleanLiteral();
		BL.setBValue(value) ; 
		VarRef.setTargetVariable(var) ; 
		AA.setAssignedTarget(VarRef) ; 
		AA.setAssignedValue(BL) ;
		return AA ; 
	}
	
	/**
	 * 
	 * @param LVariable v1 v2 v3
	 * @param value false false false
	 * @return v1:= false ; v2:= false ; v3 := false ; 
	 */
	
	public static CompositeAction CreateAssignmentActions(List<Variable> LVariable , boolean value) {
		CompositeAction CA = actionFactory.createCompositeAction() ; 
		for(Object o : LVariable) {
			Variable v = (Variable) o ; 
			CA.getContent().add(CreateAssignmentActionBoolVar(v,value)) ; 
		}
		return CA ; 
	}
	
	/**
	 * 
	 * @param v   v1
	 * @param value   2
	 * @param Operator = true then equals else not equal
	 * @return  v1 == 2 (if operator == true) else v1 != 2
	 */
	public static BinaryExpression CreateGuardEqual(Variable v , Integer value , boolean Operator) {
		BinaryExpression BE = expressionFactory.createBinaryExpression() ; 
		VariableReference VarRef = expressionFactory.createVariableReference() ; 
		VarRef.setTargetVariable(v) ; 
		IntegerLiteral IL = expressionFactory.createIntegerLiteral();
		IL.setIValue(value);
		BE.setLeftOperand(VarRef) ;
		BE.setRightOperand(IL) ; 
		BinaryOperator BO = null ; 
		if(Operator == true)
			BO = BinaryOperator.EQUALITY;
		else 
			BO = BinaryOperator.INEQUALITY ; 
		BE.setOperator(BO) ; 
		return BE ; 
	}
	

	
	


	/**
	 * 
	 * @param V_Target
	 * @param V_Value
	 * @return V_Target := V_Value ; 
	 */
	public static AssignmentAction CreateAssignmentAction(Variable V_Target , Variable V_Value) {
		AssignmentAction AA = actionFactory.createAssignmentAction() ; 
		
		VariableReference VarRefTarget = expressionFactory.createVariableReference() ; 
		VarRefTarget.setTargetVariable(V_Target) ; 
		
		VariableReference VarRefValue = expressionFactory.createVariableReference() ; 
		VarRefValue.setTargetVariable(V_Value) ; 

		AA.setAssignedTarget(VarRefTarget) ; 
		AA.setAssignedValue(VarRefValue) ; 
		return AA ; 
	}

	/**
	 * 
	 * @param v variable 
	 * @param value 2
	 * @return v := v + 2 ; 
	 */
	public static AssignmentAction CreateAddAssignmentAction(Variable v , Integer value) {
		AssignmentAction AA = actionFactory.createAssignmentAction() ; 
		
		// v
		VariableReference VarRef = expressionFactory.createVariableReference() ; 
		VarRef.setTargetVariable(v) ; 
		
		// v + 2
		VariableReference VarRef1 = expressionFactory.createVariableReference() ; 
		VarRef1.setTargetVariable(v) ; 
		IntegerLiteral IL = expressionFactory.createIntegerLiteral();
		IL.setIValue(value);
		BinaryOperator BA = BinaryOperator.ADDITION ; 
		BinaryExpression BE = expressionFactory.createBinaryExpression() ; 
		BE.setLeftOperand(VarRef) ; 
		BE.setOperator(BA) ; 
		BE.setRightOperand(IL) ; 
		
		AA.setAssignedTarget(VarRef1) ; 
		AA.setAssignedValue(BE) ; 

		return AA ; 
	}
	

	
	/**
	 * 
	 * @param v 
	 * @param ActualData  v1 v2 , 3
	 * @return v := f(v1,v2,3) ; 
	 */
	
	@SuppressWarnings("unchecked")
	public static AssignmentAction CreateVarFunctionAssignmentAction(Variable v , String FunctionName , @SuppressWarnings("rawtypes") List ActualData ) {
		FunctionCallExpression fce = expressionFactory.createFunctionCallExpression() ; 
		fce.setFunctionName(FunctionName) ; 
		fce.getActualData().addAll(ActualData) ; 
		
		AssignmentAction AA = actionFactory.createAssignmentAction() ; 
		VariableReference VarRef = expressionFactory.createVariableReference() ; 
		VarRef.setTargetVariable(v) ;
		
		AA.setAssignedTarget(VarRef) ; 
		AA.setAssignedValue(fce) ; 
		return AA ; 
	}
	
	/**
	 * 
	 * @param ActualData  v1 v2 , 3
	 * @return f(v1,v2,3) ; 
	 */
	
	@SuppressWarnings("unchecked")
	public static FunctionCallExpression CreateFunction(String FunctionName , @SuppressWarnings("rawtypes") List ActualData ) {
		FunctionCallExpression fce = expressionFactory.createFunctionCallExpression() ; 
		fce.setFunctionName(FunctionName) ; 
		fce.getActualData().addAll(ActualData) ; 
		return fce ; 
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static IntegerLiteral CreateIntegerLiteral(Integer value) {
		IntegerLiteral IL = expressionFactory.createIntegerLiteral() ; 
		IL.setIValue(value) ; 
		return IL ; 
	}
	
	
	public static CompositeAction CreateCompositeAction(List<Action> LAction) {
		CompositeAction CA = actionFactory.createCompositeAction() ; 
		CA.getContent().addAll(LAction) ; 
		return CA ; 
	}
	
	/**
	 * 
	 * @param Path
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static CompoundType parseBIPFile(String Path) {
		//Parse BIP file
		try {	
			CmdLineError err = new CmdLineError() ;
			Module[] bipModel ;
			ArrayList includeDirectories = new ArrayList() ;
			Map libFullNames = new HashMap() ;
			String includeDir = "" ;
			includeDirectories.add(includeDir+ "/") ;
			bipModel = Parser.parse(Path, includeDirectories, libFullNames,err);
			ujf.verimag.bip.Core.Modules.System sys = (ujf.verimag.bip.Core.Modules.System) bipModel[0];
			top = sys.getRoot();
			CompoundType CT = (CompoundType) top.getType();
			return CT;
		}
		catch(Exception E) {
			System.out.println("Error Parsing BIP File");
			return null;
		}
	}
	
	/**
	 * 
	 * @param FileName
	 * @param BIPSystem
	 * @throws FileNotFoundException
	 */
	public static void CreateBIPFile(String FileName, Module BIPSystem) throws FileNotFoundException {
		FileOutputStream out = new FileOutputStream(FileName); 
		PrintStream X = new PrintStream(out);
		Reverse a= new Reverse(X);
		a.decompile(BIPSystem);
	}
	
	
	public static CompoundType CreateCompoundType(String Name) {
		CompoundType CT = interFactory.createCompoundType() ; 
		CT.setName(Name) ; 
		return CT ; 
	}
	
	public static Component getComponent(CompoundType compoundType , String Name) {
		for(Object o : compoundType.getSubcomponent()) {
			Component component = (Component) o ; 
			if(component.getName().equals(Name))
				return component ; 
		}
		return null ; 
	}
	
	public static Port getPort (ComponentType componentType , String PortName) {
		for(Object o : componentType.getPort()) {
			Port p = (Port) o ; 
			if(p.getName().equals(PortName))
				return p ; 
		}
		return null ; 
	}
	
	
	public static boolean IsAllAtomic(CompoundType compoundType) {
		for (Object o : compoundType.getSubcomponent()) {
			Component component = (Component) o ; 
			if(!(component.getType() instanceof AtomType))
				return false ; 
		}
		return true ; 
	}
	
	public static Component getNonAtomicComponent(CompoundType compoundType) {
		for(Object o : compoundType.getSubcomponent()) {
			Component component = (Component) o ; 
			if(component.getType() instanceof CompoundType)
				return component ; 
 		}
		return null ; 
	}
	
	
	/**
	 * 
	 * @param LIPR comp1.p1 comp2.p2 comp3.p3 
	 * @param IPR comp1.p1 (as object it may be different than comp1.p1 in LIPR)
	 * @return this method return if LIPR contain IPR with the port name is p1 and component name is comp1
	 */
	public static InnerPortReference ContainsInnerPortReference(List<InnerPortReference> LIPR , InnerPortReference IPR) {
		for(Object o : LIPR) {
			InnerPortReference ipr = (InnerPortReference) o ; 
			if(IPR.getTargetPort().getName().equals(ipr.getTargetPort().getName()) && 
					IPR.getTargetInstance().getTargetPart().getName().equals(ipr.getTargetInstance().getTargetPart().getName())) 
				return ipr ; 
		}
		return null ; 
	}
	
	
	public static InnerPortReference ContainsInnerPortReference(@SuppressWarnings("rawtypes") Map MapIPR , InnerPortReference IPR) {
		for(Object o : MapIPR.keySet()) {
			InnerPortReference ipr = (InnerPortReference) o ; 
			if(IPR.getTargetPort().getName().equals(ipr.getTargetPort().getName()) && 
					IPR.getTargetInstance().getTargetPart().getName().equals(ipr.getTargetInstance().getTargetPart().getName())) 
				return ipr ; 
		}
		return null ; 
	}
	
	private static boolean IsIntersectState(List<State> LState1, List<State> LState2)
	{
		for(Object o1 : LState1) {
			for(Object o2 : LState2) {
				if(o1.equals(o2))
					return true;
			}
		}
		return false;
	}
	
	
	public static boolean IsConflictPetriNet(PetriNet PN)
	{
		for(final Object o1 : PN.getTransition())
		{
			final Transition t1 = (Transition) o1 ;
			for(final Object o2 : PN.getTransition())
			{
				final Transition t2 = (Transition) o2;
				final List<State> LState1 = t1.getOrigin();
				final List<State> LState2 = t2.getOrigin();
				if( !t1.equals(t2) && IsIntersectState(LState1,LState2) && (LState1.size() > 1 || LState2.size() > 1))
				{
					System.out.println("Error input : you do not have the right to use Petri Net with conflict.");
					System.out.println("Verify atom type :  " + PN.getAtomType().getName() + "\nConflicts Transitions Labelled by the ports : \n" + ((PortDefinitionReference)t1.getTrigger()).getTarget().getName() + "\n" + ((PortDefinitionReference)t2.getTrigger()).getTarget().getName());
					return true ;
				}
			}	
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param OpaqueString
	 * @return
	 */
	public static OpaqueElement CreateOpaqueElement(String OpaqueString){
		OpaqueElement Oelem = moduleFactory.createOpaqueElement();
		Oelem.setBody(OpaqueString) ; 
		return Oelem ; 

	}
	
	public static List<DataParameter> getCopyDataParameter(List<DataParameter> LDataParameter){
		List<DataParameter> LDataParameterCopy = new java.util.LinkedList<DataParameter>() ; 
		for(Object o : LDataParameter) {
			DataParameter dataparametercopy = getCopy((DataParameter) o ) ; 
			LDataParameterCopy.add(dataparametercopy) ; 
		}
		return LDataParameterCopy ;	
	}
	
	/**
	 * @param pdr
	 * @return the corresponding port of pdr. 
	 */
	public static Port getPort(PortExpression portExpression) {
		PortDefinitionReference pdr = (PortDefinitionReference) portExpression; 
		PortDefinition pd = pdr.getTarget();
		AtomType at = pd.getAtomType();
		for(Port p : at.getPort()) {
			DefinitionBinding db = (DefinitionBinding) p.getBinding();
			if(db.getDefinition() == pd)
				return p;
		}
		return null;
	}
	
	/**
	 * 
	 * @param p
	 * @return all the transition labeled with the port p
	 */
	public static List<Transition> getTransitions(Port p) {
		List<Transition> transitions = new LinkedList<Transition>();
		assert(p.getComponentType() instanceof AtomType);
		AtomType at = (AtomType) p.getComponentType();
		PetriNet pn = (PetriNet) at.getBehavior();
		DefinitionBinding db = (DefinitionBinding) p.getBinding();
		for(Transition t: pn.getTransition()) {
			PortDefinitionReference pdr = (PortDefinitionReference) t.getTrigger();
			if(pdr.getTarget() == db.getDefinition())
				transitions.add(t);
		}
		return transitions; 	
	}
	
	/**
	 * MODIFIES: variables -> all the assigned variables given an action a
	 * @param a
	 */
	private static void getAssignedVariables(Action a, Map<Variable, AssignmentAction> variables) {
		if(a instanceof AssignmentAction) {
			variables.put(
				((VariableReference)((AssignmentAction) a).getAssignedTarget()).getTargetVariable(), (AssignmentAction) a
			);
		}
		else if(a instanceof CompositeAction) {
			for(Action subAction: ((CompositeAction) a).getContent()) {
				getAssignedVariables(subAction, variables);
			}
		}
		else {
			//TODO
		}
	}
	
	/**
	 * 
	 * @param a
	 * @return all the assigned variables given an Action a
	 */
	public static Map<Variable, AssignmentAction> getAssignedVariables(Action a) {
		Map<Variable, AssignmentAction> variables = new HashMap<Variable, AssignmentAction>();
		getAssignedVariables(a, variables);
		return variables; 
	}
	
	/**
	 * 
	 * @param v
	 * @return all the transitions that update the variable v
	 */
	public static List<Transition> getTransitionsUpdateVariable(Variable v) {
		assert(v.eContainer() instanceof AtomType);
		AtomType at = (AtomType) v.eContainer();
		PetriNet pn = (PetriNet) at.getBehavior();
		
		List<Transition> transitions = new LinkedList<Transition>();
		
		for(Transition t : pn.getTransition()) {
			if(getAssignedVariables(t.getAction()).keySet().contains(v)) {
				transitions.add(t);
			}
		}
		
		return transitions; 
	}
	

	
	/**
	 * MODIFIES: a
	 * @param a
	 * @param v
	 * @return the first assignment action in a that modifies v
	 */
	private static void getAssignmentAction(Action a, Variable v, List<AssignmentAction> ac) {
		if(a instanceof AssignmentAction) {
			if(((VariableReference)((AssignmentAction) a).getAssignedTarget()).getTargetVariable().getName().equals(v.getName())) {
				ac.add((AssignmentAction)a);
				return;
			}
		}
		else if(a instanceof CompositeAction) {
			for(Action subAction: ((CompositeAction) a).getContent()) {
				getAssignmentAction(subAction, v, ac);
			}
		}
	}
	
	

	/**
	 * MODIFIES: a
	 * @param a
	 * @param v
	 * @return the first assignment action in a that modifies v
	 */
	private static AssignmentAction getAssignmentAction(Action a, Variable v) {
		List<AssignmentAction> assignmentActions = new LinkedList<AssignmentAction>();
		getAssignmentAction( a,  v, assignmentActions) ;
		if(assignmentActions.size() == 1) 
			return assignmentActions.get(0);
		return null;
	}
	
	/**
	 * 
	 * @param t
	 * @param v
	 * @return the first assignment action in transition t that modifies v. 
	 */
	public static AssignmentAction getAssignmentAction(Transition t, Variable v) {
		return getAssignmentAction(t.getAction(), v);
	}
	
	/**
	 * 
	 * @param c
	 * @param rdpr
	 * @return a map which contains a variable and its corresponding component given a connector and 
	 * 	a required data parameter reference (p1.z)
	 */
	public static Map<Variable,Component> getVariable(Connector c, RequiredDataParameterReference rdpr) {
		ConnectorType connType = c.getType();
		// connector => A.p1 B.p2 C.p3, connector type p0, p1, p2, rdpr = p1.z, then indexInnerPortReference = 1; 
		int indexInnerPortReference = connType.getPortParameter().indexOf(rdpr.getPortReference().getTarget());
				
		if(indexInnerPortReference < 0) return null; 
		// connector type p0, p1, p2, port parameter p1, and p1(x, y, z), indexDataParameterPort = 2 (index of z). 
		PortParameter pp = connType.getPortParameter().get(indexInnerPortReference);
		int indexDataParameterPort = pp.getType().getDataParameter().indexOf(rdpr.getTargetParameter());
		if(indexDataParameterPort < 0) return null; 

		InnerPortReference ipr = (InnerPortReference) c.getActualPort().get(indexInnerPortReference);
		Map<Variable, Component> mapVarComp = new HashMap<Variable, Component>();
		mapVarComp.put(((PortDefinition)((DefinitionBinding)ipr.getTargetPort().getBinding()).getDefinition()).getExposedVariable().get(indexDataParameterPort), 
				(Component) ipr.getTargetInstance().getTargetPart());
		return mapVarComp;
	}
	
	
	/**
	 * MODIFIES: dataReferences -> all the assigned data references given an action a
	 * @param a
	 */
	private static void getAssignedDataReferences(Action a, Map<DataReference, AssignmentAction> dataReferences) {
		if(a instanceof AssignmentAction) {
			dataReferences.put(
				((DataReference)((AssignmentAction) a).getAssignedTarget()), (AssignmentAction) a
			);
		}
		else if(a instanceof CompositeAction) {
			for(Action subAction: ((CompositeAction) a).getContent()) {
				getAssignedDataReferences(subAction, dataReferences);
			}
		}
		else {
			//TODO
		}
	}
	
	/**
	 * 
	 * @param a
	 * @return all the assigned data references given an Action a
	 */
	public static Map<DataReference, AssignmentAction> getAssignedDataReferences(Action a) {
		Map<DataReference, AssignmentAction> dataReferences = new HashMap<DataReference, AssignmentAction>();
		getAssignedDataReferences(a, dataReferences);
		return dataReferences; 
	}
	
	
	/**
	 * REQUIRES: each connector contains exactly only one strong-rendez-vous interaction. 
	 * 	and each connector contains at maximum one assignment action which modifies the rdpr corresponding to the variable v. 
	 * @param c
	 * @param v
	 * @return all the assignment actions with their corresponding connectors that modifies the variable v 
	 * 	in the component c
	 */
	public static Map<Connector, AssignmentAction> getAssignmentActions(Component c, Variable v) {
		Map<Connector, AssignmentAction> mapConnAction = new HashMap<Connector, AssignmentAction>();
		for(Connector connector: c.getCompoundType().getConnector()) {
			if(connector.getType().getInteractionSpecification().size() == 0) continue;
			
			InteractionSpecification interactionSpec = connector.getType().getInteractionSpecification().get(0);
			Map<DataReference, AssignmentAction> mapDataRefAction = getAssignedDataReferences(interactionSpec.getDownAction());
			for(DataReference dr : mapDataRefAction.keySet()) {
				RequiredDataParameterReference rdpr = (RequiredDataParameterReference) dr;
				Map<Variable,Component> mapVarComp = getVariable(connector, rdpr);
				if(mapVarComp.containsKey(v) && mapVarComp.get(v).equals(c)) {
					mapConnAction.put(connector, mapDataRefAction.get(dr));
				}
			}
		}
		return mapConnAction; 
	}
	
	


}
