package ch.bifrost.server.impl.session;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.api.session.SessionPacketSender;
import ch.bifrost.core.impl.session.NetworkEndointForSessionConverter;

/**
 * Represents the access point to the network for a server session adapter after multiplexing.
 */
public class ServerNetworkEndpointForSessionConverter extends NetworkEndointForSessionConverter {

	private final BlockingQueue<SessionPacket> receivedMessages;
	
	public ServerNetworkEndpointForSessionConverter(SessionPacketSender sessionPacketSender, BlockingQueue<SessionPacket> receivedMessages, String sessionId) {
		super(sessionPacketSender, sessionId);
		this.receivedMessages = receivedMessages;
	}


	@Override
	protected SessionPacket internalReceive() throws InterruptedException {
		SessionPacket sessionPacket = receivedMessages.take();
		super.counterpartAddress(sessionPacket.getCounterpartAddress());
		super.counterpartPort(sessionPacket.getCounterpartPort());
		return sessionPacket;
	}


	@Override
	protected Optional<SessionPacket> internalReceive(long timeout, TimeUnit unit) throws InterruptedException {
		SessionPacket packet = receivedMessages.poll(timeout, unit);
		if (packet == null) {
			return Optional.absent();
		}
		super.counterpartAddress(packet.getCounterpartAddress());
		super.counterpartPort(packet.getCounterpartPort());
		return Optional.of(packet);
	}
}
