package parser;

public class InterpreterException extends Exception {
	
	private static final long serialVersionUID = -7668292473320827947L;

	/**
	 * Creates a ParsingException with a failure message
	 * 
	 * @param message message about what is wrong with the query
	 */
	public InterpreterException(String message) {
		super(message);
	}	
}
