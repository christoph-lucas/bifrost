package ch.bifrost.server.api.server;

/**
 * A process to be started after multiplexing and building the session.
 */
public interface ServerProcess extends Runnable {

	/**
	 * Cancels the process. Might or might not have an effect, or might have a timeout before taking effect.
	 */
	void cancel ();

}
