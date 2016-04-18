package ch.bifrost.core.impl.session;

import java.io.IOException;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.session.SessionInternalMessage;
import ch.bifrost.core.api.session.SessionInternalMessageSender;

public class SessionInternalMessageSenderImpl implements SessionInternalMessageSender {

	private DatagramEndpoint endpoint;

	public SessionInternalMessageSenderImpl(DatagramEndpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	@Override
	public void send(SessionInternalMessage sessionPacket) throws IOException {
		endpoint.send(sessionPacket.toDatagramMessage());
	}

}
