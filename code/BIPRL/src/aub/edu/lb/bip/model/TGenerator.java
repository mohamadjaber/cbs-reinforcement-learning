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

public class TGenerator {
	private static String indent = "";
	private TCompound tCompound;

	private PrintStream output;


	private String counterVarName = "__cycle_num__";

	private static void indent() {
		indent += TogetherSyntax.tabSpace;
	}

	private static void deindent() {
		indent = indent.substring(TogetherSyntax.tabSpace.length());
	}

	public TGenerator(TCompound tCompound, String fileName) {
		this.tCompound = tCompound;
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

	private void createFooters() {
		output.println("}"); // end main
	}

	private void createHeaders() {
		output.println("#include<iostream>");
		output.println("#include<stdlib.h>");
		output.println("#include<time.h>");
		output.println("#include <unordered_map>");
		output.println("#include <string>");

		output.println("using namespace std;");
		output.println("#define wire");
		output.println("#define boolean bool");

		output.println(indent + "int main() {");
		indent();

		output.println(indent + "int " + counterVarName + " = 0;");
		output.println(indent + "int __sed = time(NULL);");
		output.println(indent + "srand (__sed);");
		output.println(indent + "cout<< \"The sed is \" << __sed << endl;");
	}

	private void decompile(TAction act) {
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
