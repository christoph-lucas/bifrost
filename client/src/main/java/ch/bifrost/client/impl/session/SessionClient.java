package ch.bifrost.client.impl.session;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.client.impl.datagram.ClientMultiplexedDatagramEndpoint;
import ch.bifrost.client.impl.keyexchange.DHKeyExchangeClient;
import ch.bifrost.client.impl.keyexchange.KeyExchangeClient;
import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.core.impl.datagram.MultiplexedDatagramEndpoint;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;

/**
 * A helper class that plugs everything together for a client on the session layer.
 */
public class SessionClient implements Closeable {

	private final KeyExchangeClient keyExchange;
	private final SessionConverterFactory sessionConverterFactory;
	private final CounterpartAddress serverPayloadAddress;

	private final DatagramEndpoint clientPayloadDatagramEndpoint;
	private final UDPDatagramEndpoint clientKeyExchangeDatagramEndpoint;

	private boolean sessionInitialized = false;
	private SessionConverter sessionConverter;

	public SessionClient (InetAddress serverHost, int serverKeyExchangePort, int serverPayloadPort, SessionConverterFactory sessionConverterFactory) throws SocketException {
		serverPayloadAddress = new CounterpartAddress(serverHost, serverPayloadPort);
		this.sessionConverterFactory = sessionConverterFactory;

		clientKeyExchangeDatagramEndpoint = new UDPDatagramEndpoint();
		CounterpartAddress serverKeyExchangeAddress = new CounterpartAddress(serverHost, serverKeyExchangePort);
		keyExchange = new DHKeyExchangeClient(clientKeyExchangeDatagramEndpoint, serverKeyExchangeAddress);

		clientPayloadDatagramEndpoint = new UDPDatagramEndpoint();
	}

	public SessionConverter initializeSession (long timeout, TimeUnit unit) throws Exception {
		if (sessionInitialized) {
			throw new RuntimeException("something happened");
		}
		Optional<IdKeyPair> optional = keyExchange.get(timeout, unit);
		if (!optional.isPresent()) {
			throw new RuntimeException("something happened");
		}
		IdKeyPair idKeyPair = optional.get();
		MultiplexedDatagramEndpoint networkAccessPoint = new ClientMultiplexedDatagramEndpoint(serverPayloadAddress, clientPayloadDatagramEndpoint, idKeyPair.getId());
		sessionConverter = sessionConverterFactory.newSessionConverter(networkAccessPoint, idKeyPair);
		sessionInitialized = true;
		return sessionConverter;
	}

	public void send (SessionMessage message) throws IOException {
		if (!sessionInitialized) {
			throw new IllegalStateException("Session not yet initialized.");
		}
		sessionConverter.send(message);
	}

	public SessionMessage receive () throws Exception {
		return sessionConverter.receive();
	}

	@Override
	public void close () throws IOException {
		if (sessionConverter instanceof Closeable) {
			((Closeable) sessionConverter).close();
		}
		clientKeyExchangeDatagramEndpoint.close();
		clientPayloadDatagramEndpoint.close();
	}

}
