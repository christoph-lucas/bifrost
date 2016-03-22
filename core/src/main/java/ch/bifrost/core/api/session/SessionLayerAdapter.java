package ch.bifrost.core.api.session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

/**
 * An abstraction of the session layer. A {@link SessionLayerAdapter} together with a (SingleSession- or Multiplexing) Receiver turns a datagram endpoint into a session endpoint.
 */
public interface SessionLayerAdapter {

	/**
	 * Send the message to the communication partner in this session.
	 * @param message the message to be sent
	 * @throws IOException thrown if an error occurrs
	 */
	void send(Message message) throws IOException;
	
	/**
	 * Blocking call to receive the next message.
	 * @return the next message
	 * @throws InterruptedException thrown when interrupted during wait for next message 
	 */
	Message receive() throws InterruptedException;

	/**
	 * Blocking call to receive the next message, waiting at most for the timeout.
	 * @return the next message or null if timeout exceeded
	 * @throws InterruptedException thrown when interrupted during wait for next message 
	 */
	Optional<Message> receive(long timeout, TimeUnit unit) throws InterruptedException;

	/**
	 * Messages belonging to this adapter have to be recognized by an ID. This ID has to be computed from
	 * the first message, so that the second message can be delivered to this adapter. 
	 * @return an id
	 */
	String computeId(SessionPacket firstMessage);
	
}
