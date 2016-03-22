package ch.bifrost.core.impl.session;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.api.session.SessionPacketSender;

/**
 * Represents an endpoint for a single session, accesses the network either after multiplexing (Server) or directly (Client).
 */
public class SingleSessionEndpoint {

	private final SessionPacketSender sessionPacketSender;
	private final BlockingQueue<SessionPacket> receivedMessages;
	private final InetAddress counterpartAddress;
	private final int counterpartPort;
	private String sessionId;

	public SingleSessionEndpoint(SessionPacketSender sessionPacketSender, BlockingQueue<SessionPacket> receivedMessages, InetAddress counterpartAddress, int counterpartPort) {
		this.sessionPacketSender = sessionPacketSender;
		this.receivedMessages = receivedMessages;
		this.counterpartAddress = counterpartAddress;
		this.counterpartPort = counterpartPort;
	}
	
	/**
	 * Send the message to the communication partner in this session.
	 * @param message the message to be sent
	 * @throws IOException thrown if an error occurrs
	 */
	public void send(Message message) throws IOException {
		SessionPacket sessionPacket = new SessionPacket(counterpartAddress, counterpartPort, sessionId, message.getContent());
		sessionPacketSender.send(sessionPacket);
	}

	/**
	 * Blocking call to receive the next message.
	 * @return the next message
	 * @throws InterruptedException thrown when interrupted during wait for next message 
	 */
	public Message receive() throws InterruptedException {
		while (true) {
			SessionPacket sessionPacket = receivedMessages.take();
			if (sessionId == null) {
				sessionId = sessionPacket.getSessionId();
			}
			if (counterpartAddress.equals(sessionPacket.getCounterpartAddress()) && counterpartPort == sessionPacket.getCounterpartPort() && sessionId.equals(sessionPacket.getSessionId())) {
				return new Message(sessionPacket.getContent());
			}
		}
	}
	
	/**
	 * Blocking call to receive the next message, waiting at most for the timeout.
	 * @return the next message or null if timeout exceeded
	 * @throws InterruptedException thrown when interrupted during wait for next message 
	 */
	public Optional<Message> receive(long timeout, TimeUnit unit) throws InterruptedException {
		while (true) {
			SessionPacket sessionPacket = receivedMessages.poll(timeout, unit);
			if (sessionPacket == null) {
				return Optional.absent();
			}
			if (sessionId == null) {
				sessionId = sessionPacket.getSessionId();
			}
			if (counterpartAddress.equals(sessionPacket.getCounterpartAddress()) && counterpartPort == sessionPacket.getCounterpartPort() && sessionId.equals(sessionPacket.getSessionId())) {
				return Optional.of(new Message(sessionPacket.getContent()));
			}
		}
	}

}
