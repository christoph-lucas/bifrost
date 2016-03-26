package ch.bifrost.client.impl.session;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionLayerAdapter;
import ch.bifrost.core.api.session.SessionLayerAdapterFactory;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import ch.bifrost.core.impl.session.SessionPacketSenderImpl;

/**
 * Plugs everything together for a client.
 */
public class ClientSessionEndpoint<T extends SessionLayerAdapter> implements Closeable {

	public static final long TIMEOUT = 100L;

	private T sessionAdapter;
	private DatagramEndpoint datagramEndpoint;

	public ClientSessionEndpoint(String serverHost, int serverPort, String sessionId, SessionLayerAdapterFactory<T> factory) throws UnknownHostException, SocketException {
		datagramEndpoint = new UDPDatagramEndpoint();
		ClientSessionAdapterNetworkAccessPoint sessionAdapterNetworkAccess = new ClientSessionAdapterNetworkAccessPoint(new SessionPacketSenderImpl(datagramEndpoint), InetAddress.getByName(serverHost), serverPort, datagramEndpoint);
		sessionAdapter = factory.newSessionLayerAdapter(sessionAdapterNetworkAccess);
	}
	
	public void send(Message message) throws IOException {
		sessionAdapter.send(message);
	}

	public Message receive() throws Exception {
		return sessionAdapter.receive();
	}

	@Override
	public void close() throws IOException {
		if (sessionAdapter instanceof Closeable) {
			((Closeable) sessionAdapter).close();
		}
		datagramEndpoint.close();
	}

	public T getSessionLayerAdapter() {
		return sessionAdapter;
	}
}
