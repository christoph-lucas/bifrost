package ch.bifrost.client.impl.session;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.Packet;
import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.api.session.SessionPacketSender;
import ch.bifrost.core.impl.session.SessionAdapterNetworkAccessPoint;

public class ClientSessionAdapterNetworkAccessPoint extends SessionAdapterNetworkAccessPoint {

	private DatagramEndpoint datagramEndpoint;

	public ClientSessionAdapterNetworkAccessPoint(SessionPacketSender sessionPacketSender, InetAddress counterpartAddress, int counterpartPort, DatagramEndpoint datagramEndpoint) {
		super(sessionPacketSender, counterpartAddress, counterpartPort);
		this.datagramEndpoint = datagramEndpoint;
	}

	@Override
	protected SessionPacket receiveWithoutTimeout() throws IOException {
			Packet receivedPacket = datagramEndpoint.receive();
			return SessionPacket.fromPacket(receivedPacket);
	}

	@Override
	protected Optional<SessionPacket> receiveWithTimeout(long timeout, TimeUnit unit) throws IOException {
		Optional<Packet> receivedPacket = datagramEndpoint.receive(timeout, unit);
		if (receivedPacket.isPresent()) {
			return Optional.of(SessionPacket.fromPacket(receivedPacket.get()));
		}
		return Optional.absent();
	}

}
