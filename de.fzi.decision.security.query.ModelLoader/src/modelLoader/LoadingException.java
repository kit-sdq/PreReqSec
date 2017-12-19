package modelLoader;

public class LoadingException extends Exception{

	private static final long serialVersionUID = 60271952872463667L;

	public LoadingException() {
		super();
	}

	public LoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoadingException(String message) {
		super(message);
	}

	public LoadingException(Throwable cause) {
		super(cause);
	}
}
