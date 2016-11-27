package aub.edu.lb.bip.api;

import ujf.verimag.bip.parser.ErrorMessage;
import ujf.verimag.bip.parser.ParserException;

public class CmdLineError implements ErrorMessage {
	
	int errorNumber= 0 ;
	int warningNumber=0 ;

	public boolean isCorrect() {
		return (errorNumber==0) ;
	}

	public Exception sendErrorMessage(int level, String message,
			int lineNumber, int columnNumber, String fileName) {
		String msg = message ;
		if (fileName!=null && lineNumber !=0) {
			msg = fileName + " " + lineNumber + ":" + columnNumber+ " " + msg ;
		}
		switch (level) {
		case ErrorMessage.BIP_NOTE: 
		{System.out.println(msg) ;
		return null ;
		}
		case ErrorMessage.BIP_WARNING: 
		{System.err.println(msg) ;
		return null;
		}
		case ErrorMessage.BIP_ERROR: 
		{System.err.println(msg) ;
		errorNumber += 1 ;
		return null;
		}
		case ErrorMessage.BIP_FATAL: 
		{System.err.println(msg) ;
		errorNumber += 1 ;
		return new ParserException();
		}
		default:
			return null ;
		}
	}

	public int getErrorNumber() {
		// TODO Auto-generated method stub
		return errorNumber;
	}

	public int getWarningNumber() {
		// TODO Auto-generated method stub
		return warningNumber;
	}

}
