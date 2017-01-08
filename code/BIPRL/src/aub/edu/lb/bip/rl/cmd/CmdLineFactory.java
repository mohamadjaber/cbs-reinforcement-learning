package aub.edu.lb.bip.rl.cmd;


import aub.edu.lb.bip.rl.DefaultSettings;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.HelpCmdLineHandler;
import jcmdline.IntParam;
import jcmdline.Parameter;
import jcmdline.StringParam;
import jcmdline.VersionCmdLineHandler;



public class CmdLineFactory {
	private static final String defaultInputBIP = "input.bip";
	private static final String helpInputBIPFile = "input BIP file name";
	private static final String cmdLineTool = "java -jar BIPRL.jar";
	
	private static final String defaultOutputFile = "output.cpp";
	private static final String helpOutputFile = "output file to be automatically generated";
	public static final String doubleErrorMessage = "good reward, badReward, probality random and gamma must be double";
	public static final String infiniteAllOrNothing = "In case of infinite option, you either set all the parameters or none (default values will be set)";
	private static final String VERSION = "V 1.0";
	
	private static final String cmdLineDescription = "A tool to enforce properties using reinforcement learning!";
	
	private static final String helpText = "Have Fun!";
		
	public static final String normal = "normal";
	public static final String finite = "value-iterator";
	public static final String infinite = "infinite";

	public static final String typeNotSupportedError = "Mode is not supported (" +
			normal + ", " + finite + ", or " + infinite;

	public StringParam mode;
	public FileParam inputBIP; 
	public FileParam outputFile; 
	public FileParam badStates; 
	
	public static final String ErrorGeneratingFile = "Error while generating source code";

	public StringParam goodReward, badReward, probaRandom, gamma;
	public IntParam episodes, epoch, hidden, capacity, minimumTrace, fair;
	public IntParam sampleCapacityPercentage, resetHistoryPeriod;
	public CmdLineHandler cmdLineHandler;
	
	public IntParam maxIterationValueIterator; 

	
	public CmdLineFactory(String[] args) {
		inputBIP = new FileParam(defaultInputBIP, helpInputBIPFile,
				FileParam.EXISTS & FileParam.IS_READABLE,
				!FileParam.OPTIONAL,
				!FileParam.MULTI_VALUED
			);
		goodReward = new StringParam("good-reward", "Good reward value (default " + DefaultSettings.goodReward + ")");
		badReward = new StringParam("bad-reward", "Bad reward value (default " + DefaultSettings.badReward + ")");
		probaRandom = new StringParam("proba-random", "Probability of exploration (default " + DefaultSettings.defaultProbabilityRandom + ")");
		gamma = new StringParam("gamma", "Gamma (default " + DefaultSettings.gamma + ")");

		episodes = new IntParam("episodes", "Number of episodes (default " + DefaultSettings.defaultNumberEpisodes + ")");
		epoch = new IntParam("epoch", "Epoch (default " + DefaultSettings.EPOCH + ")");
		hidden = new IntParam("hidden", "Number of neurons in hidden layer (default " + DefaultSettings.defaultNumberOfNeuronsHidden + ")");
		capacity = new IntParam("capacity", "Capacity of memory replay (default " + DefaultSettings.defaultCapacityReplay + ")");
		minimumTrace = new IntParam("trace", "Minimum trace length (trace length is guaranteed to be greater than \"minimum trace length\" and \"diameter of all atomic components\" - default minimum trace length is " + DefaultSettings.minimumTraceLengthIteration + ")");
		sampleCapacityPercentage = new IntParam("sample-capacity", "Size of samples used for training (default " + DefaultSettings.defaultSampleCapacityPercentage + "%)");
		resetHistoryPeriod = new IntParam("period-reset", "Reset period time (default " + DefaultSettings.defaultResetHistoryTime + ")");
		fair = new IntParam("Fairness degree", "Fairness degree (default no fairness, i.e., <= 0)");
		maxIterationValueIterator = new IntParam("max-iteration", "Bound iteration (default " + DefaultSettings.DefaultMaxIteration + ")");

		mode = new StringParam("mode", "\n*" + 
		finite + ": for finite case \n\toptions used: " + 
					gamma.getTag() + ", " + goodReward.getTag() + ", " + 
					maxIterationValueIterator.getTag() + "\n*" + 
		infinite + ": for infinite case.\n\toptions used: " + 
			"all except " + maxIterationValueIterator.getTag() + "\n*" + 
		normal + ": without reinforcement learning\n\t(no options - default)\n");
	
		outputFile = new FileParam(defaultOutputFile, helpOutputFile,
				FileParam.DOESNT_EXIST | FileParam.EXISTS | FileParam.IS_READABLE,
				!FileParam.OPTIONAL,
				!FileParam.MULTI_VALUED
			);
		
		badStates = new FileParam("badStates.txt", "File containing bad states to be avoided",
				FileParam.EXISTS & FileParam.IS_READABLE,
				!FileParam.OPTIONAL,
				!FileParam.MULTI_VALUED
			);
		

		cmdLineHandler = new VersionCmdLineHandler(VERSION,
				(CmdLineHandler) new HelpCmdLineHandler(helpText, cmdLineTool, cmdLineDescription,
						new Parameter[] { mode, maxIterationValueIterator, episodes, goodReward, badReward, epoch, hidden, 
								probaRandom, gamma, capacity, minimumTrace, sampleCapacityPercentage,
								resetHistoryPeriod, fair },
						new Parameter[] { inputBIP, outputFile, badStates} ));
		
		cmdLineHandler.parse(args);	
	}
	

	public String getInputBIPFile() {
		return inputBIP.getValue().getAbsolutePath();
	}
	
	public String getOutputFile() {
		return outputFile.getValue().getAbsolutePath();
	}
	
	public String getBadStates() {
		return badStates.getValue().getAbsolutePath();
	}
	
	public boolean validDoubleInfinite() {
		try {
			Double.parseDouble(goodReward.getValue());
			Double.parseDouble(badReward.getValue());
			Double.parseDouble(probaRandom.getValue());
			Double.parseDouble(gamma.getValue());
			return true;
		} catch(Exception e) {
			return false;
		}
	}
 
}
