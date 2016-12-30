package aub.edu.lb.bip.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import aub.edu.lb.bip.api.TogetherSyntax;
import aub.edu.lb.bip.expression.TAction;
import aub.edu.lb.bip.expression.TAssignmentAction;
import aub.edu.lb.bip.expression.TCompositeAction;
import aub.edu.lb.bip.expression.TDoTogetherAction;
import aub.edu.lb.bip.expression.TExpression;
import aub.edu.lb.bip.expression.TIfAction;
import aub.edu.lb.bip.expression.TWhileAction;
import aub.edu.lb.bip.rl.DeepReinforcementLearning;

public class TGenerator {
	protected DeepReinforcementLearning deepRL; 
	protected static String indent = "";
	protected TCompound tCompound;

	protected PrintStream output;


	protected String counterVarName = "__cycle_num__";

	protected static void indent() {
		indent += TogetherSyntax.tabSpace;
	}

	protected static void deindent() {
		indent = indent.substring(TogetherSyntax.tabSpace.length());
	}

	public TGenerator(TCompound tCompound, String fileName) {
		this.tCompound = tCompound;
		if(tCompound instanceof TCompoundDeepReinforcementLearning)
			this.deepRL = ((TCompoundDeepReinforcementLearning)tCompound).deepRL; 
		try {
			output = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("Cannot create output ABC file.");
			System.exit(0);
		}
		createHeaders();
		decompile(tCompound.getTogetherAction());
		createFooters();
	}
	
	protected void createFooters() {
		output.println("}"); // end main
	}
	
	protected void createHeaders() {
		output.println("#include<iostream>");
		output.println("#include<stdlib.h>");
		output.println("#include<time.h>");
		output.println("#include <unordered_map>");
		output.println("#include <string>");
		output.println("#include <math.h>");


		output.println("using namespace std;");
		output.println("#define wire");
		output.println("#define boolean bool");

		if(tCompound instanceof TCompoundDeepReinforcementLearning)
			initializeDeepRL();
		
		output.println(indent + "int main() {");
		indent();

		output.println(indent + "int " + counterVarName + " = 0;");
		output.println(indent + "int __sed = time(NULL);");
		output.println(indent + "srand (__sed);");
		output.println(indent + "cout<< \"The sed is \" << __sed << endl;");
		if(tCompound instanceof TCompoundDeepReinforcementLearning)
			initializeWeights();
	}
	
	private void initializeDeepRL() {
		output.println("const double " + TogetherSyntax.bias0 + " = " + deepRL.getBias()[0] + ";");
		output.println("const double " + TogetherSyntax.bias1 + " = " + deepRL.getBias()[1] + ";");
		
		output.println("const double " + TogetherSyntax.layer0 + " = " + deepRL.getInputNetworkSize() + ";");
		output.println("const double " + TogetherSyntax.layer1 + " = " + deepRL.getHiddenLayerNetworkSize() + ";");
		output.println("const double " + TogetherSyntax.layer2 + " = " + deepRL.getOutputNetworkSize() + ";");

		output.println("double ** " + TogetherSyntax.weightsMatrixInput + ";");
		output.println("double ** " + TogetherSyntax.weightsMatrixOutput + ";");

		injectSigmoid();
		injectProduct();
		injectComputeOutput();
		injectInputStateVar();
		
	}
	
	private void initializeWeights() {
		output.println(indent + TogetherSyntax.weightsMatrixInput + " = new double*[" + deepRL.getInputNetworkSize() + "];");
		output.println(indent + TogetherSyntax.weightsMatrixOutput + " = new double*[" + deepRL.getHiddenLayerNetworkSize() + "];");
		output.println(indent + "for(int i = 0; i < " + deepRL.getInputNetworkSize() + "; i++) " 
				+ TogetherSyntax.weightsMatrixInput + "[i] = new double[" + deepRL.getHiddenLayerNetworkSize() + "];");
		
		output.println(indent + "for(int i = 0; i < " + deepRL.getHiddenLayerNetworkSize() + "; i++) " 
				+ TogetherSyntax.weightsMatrixOutput + "[i] = new double[" + deepRL.getOutputNetworkSize() + "];");
		
		
		// copy weights
		double[][][] weights = deepRL.getWeights();
		for(int i = 0; i < deepRL.getInputNetworkSize(); i++) {
			for(int j = 0; j < deepRL.getHiddenLayerNetworkSize() - 1; j++) {
				output.println(indent + TogetherSyntax.weightsMatrixInput + "[" + i +"][" + j + "] = " + weights[0][i][j] + ";");
			}
		}
		
		for(int i = 0; i < deepRL.getHiddenLayerNetworkSize(); i++) {
			for(int j = 0; j < deepRL.getOutputNetworkSize(); j++) {
				output.println(indent + TogetherSyntax.weightsMatrixOutput + "[" + i +"][" + j +"] = " + weights[1][i][j] + ";");
			}
		}
	}
	
	private void injectInputStateVar() {
		output.println("double * " + TogetherSyntax.current_state_identifier + " = new double[" + deepRL.getInputNetworkSize() + "];");
	}
	
	private void injectSigmoid() {
		output.println("inline double sigmoid(double x) {");
		indent();
		output.println(indent + "return (1.0 / (1 + exp(-x) )); ");
		deindent();
		output.println("}");	
	}
	
	private void injectProduct() {
		output.println("double* product(double* input, double** weights, int l, int c, double bias, boolean isSigmoid) {");
		indent();
		output.println(indent + "double* output = new double[c + 1]; // +1 for bias input (optimization)");
		output.println(indent + "for(int j = 0; j < c - 1; j++)  {");
		indent();
		output.println(indent + "output[j] = 0;");
		output.println(indent + "for(int i = 0; i < l; i++) {");
		indent();
		output.println(indent + "output[j] += input[i] * weights[i][j];");
		deindent();
		output.println(indent + "}");
		output.println(indent + "if(isSigmoid) output[j] = sigmoid(output[j]);");
		deindent();
		output.println(indent + "}");
		output.println(indent + "output[c - 1] = bias;");
		output.println(indent + "return output;");
		deindent();
		output.println("}");
	}
	
	private void injectComputeOutput() {
		output.println("double* " + TogetherSyntax.forwardProp + "(double* input, int size) {");
		indent();
		output.println(indent + "double *output1 = product(input, weights1, layer0, layer1, bias1, true);");
		output.println(indent + "return product(output1, weights2, layer1, layer2, 0, false);");
		deindent();
		output.println("}");
	}
	
	protected void decompile(TAction act) {
		if (act instanceof TExpression)
			output.println(indent + act);
		else if (act instanceof TAssignmentAction) {
			output.println(indent + act);
		} else if (act instanceof TCompositeAction) {
			for (TAction a : ((TCompositeAction) act).getContents()) {
				decompile(a);
			}
		} else if (act instanceof TWhileAction) {
			TWhileAction action = (TWhileAction) act;
			output.print(indent + TogetherSyntax.while_loop + "(");
			output.print(action.getCondition());
			output.print(")");
			output.println(indent + " {");
			indent();
			decompile(action.getAction());
			deindent();
			output.println(indent + "}");
		} else if (act instanceof TDoTogetherAction) {
			TDoTogetherAction action = (TDoTogetherAction) act;
			decompile(action.getAction());
		} else if (act instanceof TIfAction) {
			TIfAction action = (TIfAction) act;
			output.print(indent + TogetherSyntax.if_condition + "(");
			output.print(action.getCondition());
			output.println(") {");
			indent();
			decompile(action.getIfCase());
			deindent();
			output.println(indent + "}");
			output.println(indent + TogetherSyntax.else_condition + " {");
			indent();
			decompile(action.getElseCase());
			deindent();
			output.println(indent + "}");
		} else {
			// throw new Error("Unimplemented");
		}
	}

	public TCompound getTCompound() {
		return tCompound;
	}

}
