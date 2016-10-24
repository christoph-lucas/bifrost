package ch.bifrost.core.api.session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

/**
 * An abstraction of the session layer. A {@link SessionConverter} together with a (SingleSession- or Multiplexing) Receiver turns a datagram endpoint into a session endpoint.
 */
public interface SessionConverter {

	/**
	 * Send the message to the communication partner in this session.
	 * 
	 * @param message the message to be sent
	 * @throws IOException thrown if an error occurrs
	 */
	void send (SessionMessage message) throws IOException;

	/**
	 * Blocking call to receive the next message.
	 * 
	 * @return the next message
	 * @throws Exception thrown if something went wrong
	 */
	SessionMessage receive () throws Exception;

	/**
	 * Blocking call to receive the next message, waiting at most for the timeout.
	 * 
	 * @return the next message or null if timeout exceeded
	 * @throws Exception thrown if something went wrong
	 */
	Optional<SessionMessage> receive (long timeout, TimeUnit unit) throws Exception;

}
