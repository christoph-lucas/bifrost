package ch.bifrost.client.impl.session;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.session.SessionInternalMessage;
import ch.bifrost.core.api.session.SessionInternalMessageSender;
import ch.bifrost.core.impl.session.NetworkEndointForSessionConverter;

public class ClientNetworkEndpointForSessionConverter extends NetworkEndointForSessionConverter {

	private DatagramEndpoint datagramEndpoint;

	public ClientNetworkEndpointForSessionConverter(SessionInternalMessageSender sessionPacketSender, InetAddress serverAddress, int serverPort, DatagramEndpoint datagramEndpoint, String sessionId) {
		super(sessionPacketSender, sessionId);
		super.counterpartAddress(serverAddress);
		super.counterpartPort(serverPort);
		this.datagramEndpoint = datagramEndpoint;
	}

	@Override
	protected SessionInternalMessage internalReceive() throws IOException {
			DatagramMessage receivedPacket = datagramEndpoint.receive();
			return SessionInternalMessage.from(receivedPacket);
	}

	@Override
	protected Optional<SessionInternalMessage> internalReceive(long timeout, TimeUnit unit) throws IOException {
		Optional<DatagramMessage> receivedPacket = datagramEndpoint.receive(timeout, unit);
		if (receivedPacket.isPresent()) {
			return Optional.of(SessionInternalMessage.from(receivedPacket.get()));
		}
		return Optional.absent();
	}

}
