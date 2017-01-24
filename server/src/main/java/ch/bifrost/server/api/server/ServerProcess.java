package ch.bifrost.server.api.server;

/**
 * A process to be started after multiplexing and building the session.
 */
public interface ServerProcess extends Runnable {

	/*
	 * Produce a unique ID with which this process can be identified.
	 */
	String getId ();

	/**
	 * Cancels the process. Might or might not have an effect, or might have a timeout before taking effect.
	 */
	void cancel ();

}
