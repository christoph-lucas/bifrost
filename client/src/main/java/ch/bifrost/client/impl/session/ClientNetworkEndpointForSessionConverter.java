package ch.bifrost.client.impl.session;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.Packet;
import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.api.session.SessionPacketSender;
import ch.bifrost.core.impl.session.NetworkEndointForSessionConverter;

public class ClientNetworkEndpointForSessionConverter extends NetworkEndointForSessionConverter {

	private DatagramEndpoint datagramEndpoint;

	public ClientNetworkEndpointForSessionConverter(SessionPacketSender sessionPacketSender, InetAddress serverAddress, int serverPort, DatagramEndpoint datagramEndpoint, String sessionId) {
		super(sessionPacketSender, sessionId);
		super.counterpartAddress(serverAddress);
		super.counterpartPort(serverPort);
		this.datagramEndpoint = datagramEndpoint;
	}

	@Override
	protected SessionPacket internalReceive() throws IOException {
			Packet receivedPacket = datagramEndpoint.receive();
			return SessionPacket.fromPacket(receivedPacket);
	}

	@Override
	protected Optional<SessionPacket> internalReceive(long timeout, TimeUnit unit) throws IOException {
		Optional<Packet> receivedPacket = datagramEndpoint.receive(timeout, unit);
		if (receivedPacket.isPresent()) {
			return Optional.of(SessionPacket.fromPacket(receivedPacket.get()));
		}
		return Optional.absent();
	}

}
