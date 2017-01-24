package ch.bifrost.client.impl.session;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import ch.bifrost.client.impl.datagram.ClientMultiplexedDatagramEndpoint.ClientMultiplexedDatagramEndpointFactory;
import ch.bifrost.client.impl.keyexchange.KeyExchangeClient;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.core.impl.datagram.MultiplexedDatagramEndpoint;

/**
 * A helper class that plugs everything together for a client on the session layer.
 */
public class SessionClient implements Closeable {

	private final KeyExchangeClient keyExchange;
	private final SessionConverterFactory sessionConverterFactory;

	private final ClientMultiplexedDatagramEndpointFactory clientMultiplexedDatagramEndpointFactory;

	private boolean sessionInitialized = false;
	private SessionConverter sessionConverter;

	@Inject
	public SessionClient (KeyExchangeClient keyExchange,
			SessionConverterFactory sessionConverterFactory,
			ClientMultiplexedDatagramEndpointFactory clientMultiplexedDatagramEndpointFactory) throws SocketException {
		this.keyExchange = keyExchange;
		this.sessionConverterFactory = sessionConverterFactory;

		this.clientMultiplexedDatagramEndpointFactory = clientMultiplexedDatagramEndpointFactory;
	}

	@SuppressWarnings("resource")
	public SessionConverter initializeSession (long timeout, TimeUnit unit) throws Exception {
		if (sessionInitialized) {
			throw new RuntimeException("something happened");
		}
		Optional<IdKeyPair> optional = keyExchange.get(timeout, unit);
		if (!optional.isPresent()) {
			throw new RuntimeException("something happened");
		}
		IdKeyPair idKeyPair = optional.get();
		MultiplexedDatagramEndpoint networkAccessPoint = this.clientMultiplexedDatagramEndpointFactory.create(idKeyPair.getId());
		sessionConverter = sessionConverterFactory.create(networkAccessPoint, idKeyPair);
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
		sessionConverter.close();
		keyExchange.close();
	}

}
