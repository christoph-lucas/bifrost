package ch.bifrost.core.impl.session;

import java.io.IOException;

import ch.bifrost.core.api.datagram.DatagramLayerAdapter;
import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.api.session.SessionPacketSender;

public class SessionPacketSenderImpl implements SessionPacketSender {

	private DatagramLayerAdapter endpoint;

	public SessionPacketSenderImpl(DatagramLayerAdapter endpoint) {
		this.endpoint = endpoint;
	}
	
	@Override
	public void send(SessionPacket sessionPacket) throws IOException {
		endpoint.send(sessionPacket.toPacket());
	}

}
