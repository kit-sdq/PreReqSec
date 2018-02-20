package de.fzi.decision.security.ui.controllers.query;

import modelLoader.InitializationException;
import modelLoader.LoadingException;
import parser.InterpreterException;

/**
 * This interface acts as a listener to start the security threat analysis
 * @author matthias endlichhofer
 *
 */

public interface IAnalysisClickListener {
	
	public abstract void startAnalysis(String attackQuery, String patternQuery) throws InterpreterException, LoadingException, InitializationException;
	
}
