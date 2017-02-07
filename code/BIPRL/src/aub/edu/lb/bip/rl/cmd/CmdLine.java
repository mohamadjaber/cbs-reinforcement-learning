package aub.edu.lb.bip.rl.cmd;

import aub.edu.lb.bip.api.Helper;
import aub.edu.lb.bip.model.TCompound;
import aub.edu.lb.bip.model.TCompoundNormal;
import aub.edu.lb.bip.model.TGenerator;
import aub.edu.lb.bip.rl.DeepReinforcementLearning;
import aub.edu.lb.bip.rl.ValueIterator;

public class CmdLine {
	static CmdLineFactory cmdLine;
	
	public static void main(String[] args)  {
		cmdLine = new CmdLineFactory(args);
		
		String bipFile = cmdLine.getInputBIPFile();
		String outputFile = cmdLine.getOutputFile();
		String badStateFile = cmdLine.getBadStates();

		String mode; 
		
		if(!cmdLine.mode.isSet()) {
			mode = CmdLineFactory.normal;
		} else mode = cmdLine.mode.getValue();
		
		
		System.out.println("Mode selected: " + mode);
		
		TCompound tCompound = null; 
		
		if(mode.equals(CmdLineFactory.infinite)) {
			tCompound = new DeepReinforcementLearning(bipFile, badStateFile);
			boolean optionsValid = setOptionsDeepLearning((DeepReinforcementLearning) tCompound);
			if(!optionsValid) {
				System.out.println(CmdLineFactory.doubleErrorMessage);
				System.exit(0);
			}
			tCompound.compute();
		} else if(mode.equals(CmdLineFactory.finite)) {
			tCompound = new ValueIterator(bipFile, badStateFile);

			boolean optionsValid = setOptionsValueIterator((ValueIterator) tCompound);
			if(!optionsValid) {
				System.out.println(CmdLineFactory.doubleErrorMessage);
				System.exit(0);
			}
			tCompound.compute();
		} else if(mode.equals(CmdLineFactory.normal)) {
			tCompound = new TCompoundNormal(bipFile);
		} else {
			System.out.println(CmdLineFactory.typeNotSupportedError);
			System.exit(0);
		}
		
		try {
			new TGenerator(tCompound, outputFile);
		} catch(Exception e) {
			System.out.println(CmdLineFactory.ErrorGeneratingFile);
			System.exit(0);
		}
	}
	
	private static boolean setOptionsValueIterator(ValueIterator tCompound) {
		if(cmdLine.maxIterationValueIterator.isSet()) tCompound.setMaxIteration(cmdLine.maxIterationValueIterator.getValue());

		
		if(cmdLine.goodReward.isSet()) {
			String goodReward = cmdLine.goodReward.getValue();
			if(!Helper.isDouble(goodReward)) return false;
			else tCompound.setGoodReward(Double.parseDouble(goodReward));
		}
		
		if(cmdLine.badReward.isSet()) {
			String badReward = cmdLine.badReward.getValue();
			if(!Helper.isDouble(badReward)) return false;
			else tCompound.setBadReward(Double.parseDouble(badReward));
		}
		
		if(cmdLine.gamma.isSet()) {
			String gamma = cmdLine.badReward.getValue();
			if(!Helper.isDouble(gamma)) return false;
			else tCompound.setGamma(Double.parseDouble(gamma));
		}		return true;
	}

	public static boolean setOptionsDeepLearning(DeepReinforcementLearning deepRL) {
		if(cmdLine.episodes.isSet()) deepRL.setEpisode(cmdLine.episodes.getValue());
		if(cmdLine.epoch.isSet()) deepRL.setEpoch(cmdLine.epoch.getValue());
		if(cmdLine.capacity.isSet()) deepRL.setCapacityReplay(cmdLine.capacity.getValue());
		
		if(cmdLine.fair.isSet()) deepRL.setFairnessDegreeDistance(Double.parseDouble(cmdLine.fair.getValue()));
		
		if(cmdLine.fair.isSet()) {
			String fairness = cmdLine.fair.getValue();
			if(!Helper.isDouble(fairness)) return false;
			else deepRL.setFairnessDegreeDistance(Double.parseDouble(fairness)); 
		}
		
		if(cmdLine.hidden.isSet()) deepRL.setHiddenCount(cmdLine.hidden.getValue());
		if(cmdLine.minimumTrace.isSet()) deepRL.setMinimumTraceLength(cmdLine.minimumTrace.getValue());
		if(cmdLine.resetHistoryPeriod.isSet()) deepRL.setResetHistoryPeriod(cmdLine.resetHistoryPeriod.getValue());
		if(cmdLine.sampleCapacityPercentage.isSet()) deepRL.setSampleCapacityPercentage(cmdLine.sampleCapacityPercentage.getValue());

		
		if(cmdLine.goodReward.isSet()) {
			String goodReward = cmdLine.goodReward.getValue();
			if(!Helper.isDouble(goodReward)) return false;
			else deepRL.setGoodReward(Double.parseDouble(goodReward));
		}
		
		if(cmdLine.badReward.isSet()) {
			String badReward = cmdLine.badReward.getValue();
			if(!Helper.isDouble(badReward)) return false;
			else deepRL.setBadReward(Double.parseDouble(badReward));
		}
		
		if(cmdLine.gamma.isSet()) {
			String gamma = cmdLine.badReward.getValue();
			if(!Helper.isDouble(gamma)) return false;
			else deepRL.setGamma(Double.parseDouble(gamma));
		}
		
		if(cmdLine.probaRandom.isSet()) {
			String proba = cmdLine.probaRandom.getValue();
			if(!Helper.isDouble(proba)) return false;
			else deepRL.setProbaRandom(Double.parseDouble(proba));
		}
		return true;
	}
		
}
