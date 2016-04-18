package ch.bifrost.server.impl.session;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.SessionInternalMessage;
import ch.bifrost.core.api.session.SessionInternalMessageSender;
import ch.bifrost.core.impl.session.NetworkEndointForSessionConverter;

/**
 * Represents the access point to the network for a server session adapter after multiplexing.
 */
public class ServerNetworkEndpointForSessionConverter extends NetworkEndointForSessionConverter {

	private final BlockingQueue<SessionInternalMessage> receivedMessages;
	
	public ServerNetworkEndpointForSessionConverter(SessionInternalMessageSender sessionPacketSender, BlockingQueue<SessionInternalMessage> receivedMessages, String sessionId) {
		super(sessionPacketSender, sessionId);
		this.receivedMessages = receivedMessages;
	}


	@Override
	protected SessionInternalMessage internalReceive() throws InterruptedException {
		SessionInternalMessage sessionPacket = receivedMessages.take();
		super.counterpartAddress(sessionPacket.getCounterpartAddress());
		super.counterpartPort(sessionPacket.getCounterpartPort());
		return sessionPacket;
	}


	@Override
	protected Optional<SessionInternalMessage> internalReceive(long timeout, TimeUnit unit) throws InterruptedException {
		SessionInternalMessage packet = receivedMessages.poll(timeout, unit);
		if (packet == null) {
			return Optional.absent();
		}
		super.counterpartAddress(packet.getCounterpartAddress());
		super.counterpartPort(packet.getCounterpartPort());
		return Optional.of(packet);
	}
}
