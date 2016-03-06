package ch.bifrost.core.impl.session;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.api.session.SessionPacketSender;

public abstract class SessionAdapterNetworkAccessPoint {

	private final SessionPacketSender sessionPacketSender;
	private final InetAddress counterpartAddress;
	private final int counterpartPort;
	private String sessionId;

	public SessionAdapterNetworkAccessPoint(SessionPacketSender sessionPacketSender, InetAddress counterpartAddress, int counterpartPort) {
		this.sessionPacketSender = sessionPacketSender;
		this.counterpartAddress = counterpartAddress;
		this.counterpartPort = counterpartPort;
	}
	
	protected abstract SessionPacket receiveWithoutTimeout() throws Exception;

	protected abstract Optional<SessionPacket> receiveWithTimeout(long timeout, TimeUnit unit) throws Exception;

	
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
	 */
	public Message receive() throws Exception {
		while (true) { // run until we have a valid message
			SessionPacket sessionPacket = receiveWithoutTimeout();
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
	 * @throws Exception thrown if something went wrong 
	 */
	public Optional<Message> receive(long timeout, TimeUnit unit) throws Exception {
		Optional<SessionPacket> sessionPacket = receiveWithTimeout(timeout, unit);
		if (!sessionPacket.isPresent()) {
			return Optional.absent();
		}
		if (sessionId == null) {
			sessionId = sessionPacket.get().getSessionId();
		}
		if (counterpartAddress.equals(sessionPacket.get().getCounterpartAddress()) && counterpartPort == sessionPacket.get().getCounterpartPort() && sessionId.equals(sessionPacket.get().getSessionId())) {
			return Optional.of(new Message(sessionPacket.get().getContent()));
		}
		// received a message with wrong counterpart, return even if the timeout is not yet over
		return Optional.absent();
	}
	
}