package ch.bifrost.core.api.session;

/**
 * Exception thrown by the session layer.
 */
@SuppressWarnings("serial")
public class SessionException extends RuntimeException {

	public SessionException(String msg) {
		super(msg);
	}
	
	public SessionException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
