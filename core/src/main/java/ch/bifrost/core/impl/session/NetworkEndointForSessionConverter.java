package ch.bifrost.core.impl.session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.session.SessionInternalMessage;
import ch.bifrost.core.api.session.SessionInternalMessageSender;
import ch.bifrost.core.api.session.SessionMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class NetworkEndointForSessionConverter {

	private final SessionInternalMessageSender sessionPacketSender;
	private final String sessionId;
	@Getter
	@Setter
	private CounterpartAddress counterpartAddress;

	public NetworkEndointForSessionConverter(SessionInternalMessageSender sessionPacketSender, String sessionId) {
		this.sessionPacketSender = sessionPacketSender;
		this.sessionId = sessionId;
	}
	
	protected abstract SessionInternalMessage internalReceive() throws Exception;

	protected abstract Optional<SessionInternalMessage> internalReceive(long timeout, TimeUnit unit) throws Exception;
	
	/**
	 * Send the message to the communication partner in this session.
	 * @param message the message to be sent
	 * @throws IOException thrown if an error occurs
	 */
	public void send(SessionMessage message) throws IOException {
		if (!counterpartAddress.isValid()) {
			throw new IllegalStateException("CounterpartAddress or port not set, cannot send message.");
		}
		SessionInternalMessage sessionPacket = new SessionInternalMessage(counterpartAddress, sessionId, message.getPayload());
		sessionPacketSender.send(sessionPacket);
	}

	/**
	 * Blocking call to receive the next message.
	 * @return the next message
	 */
	public SessionMessage receive() throws Exception {
		while (true) { // run until we have a valid message
			SessionInternalMessage sessionPacket = internalReceive();
			if (sessionId.equals(sessionPacket.getSessionId())) {
				return SessionMessage.from(sessionPacket);
			}
		}
	}
	
	/**
	 * Blocking call to receive the next message, waiting at most for the timeout.
	 * @return the next message or null if timeout exceeded
	 * @throws Exception thrown if something went wrong 
	 */
	public Optional<SessionMessage> receive(long timeout, TimeUnit unit) throws Exception {
		Optional<SessionInternalMessage> sessionPacket = internalReceive(timeout, unit);
		if (!sessionPacket.isPresent()) {
			return Optional.absent();
		}
		if (sessionId.equals(sessionPacket.get().getSessionId())) {
			return Optional.of(SessionMessage.from(sessionPacket.get()));
		}
		// received a message with wrong counterpart, return even if the timeout is not yet over
		return Optional.absent();
	}
	
}