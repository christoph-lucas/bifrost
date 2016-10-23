package ch.bifrost.server.impl.session;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;

import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import ch.bifrost.server.impl.datagram.DatagramEndpointMultiplexer;
import ch.bifrost.server.impl.keyexchange.KeyExchangeServer;
import ch.bifrost.server.impl.server.EchoServer.EchoServerFactory;

/**
 * A helper class that plugs everything together for a server on the session layer.
 */
public class SessionServer implements Closeable {

	private final UDPDatagramEndpoint serverKeyExchangeDatagramEndpoint;
	private final UDPDatagramEndpoint serverPayloadDatagramEndpoint;
	private final SessionInitializer server;

	public SessionServer (int serverKeyExchangePort, int serverPayloadPort, SessionConverterFactory sessionConverterFactory) throws SocketException {
		serverKeyExchangeDatagramEndpoint = new UDPDatagramEndpoint(serverKeyExchangePort);
		KeyExchangeServer keyExchangeServer = new KeyExchangeServer(serverKeyExchangeDatagramEndpoint);
		serverPayloadDatagramEndpoint = new UDPDatagramEndpoint(serverPayloadPort);
		DatagramEndpointMultiplexer multiplexer = new DatagramEndpointMultiplexer(serverPayloadDatagramEndpoint);
		// TODO make ServerFactory a parameter
		server = new SessionInitializer(keyExchangeServer, multiplexer, sessionConverterFactory, new EchoServerFactory());
		server.start();
	}

	@Override
	public void close () throws IOException {
		server.cancel();
		serverKeyExchangeDatagramEndpoint.close();
		serverPayloadDatagramEndpoint.close();
	}

}
