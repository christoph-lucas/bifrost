package ch.bifrost.server.impl.session;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.api.session.SessionPacketSender;
import ch.bifrost.core.impl.session.SessionAdapterNetworkAccessPoint;

/**
 * Represents the access point to the network for a server session adapter after multiplexing.
 */
public class ServerSessionAdapterNetworkAccessPoint extends SessionAdapterNetworkAccessPoint {

	private final BlockingQueue<SessionPacket> receivedMessages;

	
	public ServerSessionAdapterNetworkAccessPoint(SessionPacketSender sessionPacketSender, BlockingQueue<SessionPacket> receivedMessages, InetAddress counterpartAddress, int counterpartPort) {
		super(sessionPacketSender, counterpartAddress, counterpartPort);
		this.receivedMessages = receivedMessages;
	}


	@Override
	protected SessionPacket receiveWithoutTimeout() throws InterruptedException {
		return receivedMessages.take();
	}


	@Override
	protected Optional<SessionPacket> receiveWithTimeout(long timeout, TimeUnit unit) throws InterruptedException {
		SessionPacket packet = receivedMessages.poll(timeout, unit);
		if (packet == null) {
			return Optional.absent();
		}
		return Optional.of(packet);
	}
}
