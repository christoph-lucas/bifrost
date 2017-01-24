package ch.bifrost.core.impl.datagram;


public class InvalidDatagramException extends Exception {

	public InvalidDatagramException(String msg) {
		super(msg);
	}
	
	public InvalidDatagramException(String msg, Throwable e) {
		super(msg, e);
	}
	
}
