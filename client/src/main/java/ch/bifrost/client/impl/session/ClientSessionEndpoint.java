package ch.bifrost.client.impl.session;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.session.IdKeyPair;
import ch.bifrost.core.api.session.KeyExchange;
import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.impl.session.NetworkEndointForSessionConverter;
import ch.bifrost.core.impl.session.SessionPacketSenderImpl;

/**
 * Plugs everything together for a client.
 */
public class ClientSessionEndpoint implements Closeable {

	public static final long TIMEOUT = 100L;

	private final KeyExchange keyExchange;
	private final SessionConverterFactory sessionConverterFactory;
	private boolean sessionInitialized = false;
	private SessionConverter sessionConverter;
	private DatagramEndpoint clientPayloadDatagramEndpoint;
	private InetAddress serverHost;
	private int serverPort;
	

	public ClientSessionEndpoint(KeyExchange keyExchange, SessionConverterFactory sessionConverterFactory, DatagramEndpoint clientPayloadDatagramEndpoint, InetAddress serverHost, int serverPort) {
		this.keyExchange = keyExchange;
		this.sessionConverterFactory = sessionConverterFactory;
		this.clientPayloadDatagramEndpoint = clientPayloadDatagramEndpoint;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}
	
	public SessionConverter initializeSession(long timeout, TimeUnit unit) throws Exception {
		if (sessionInitialized) {
			throw new RuntimeException("something happened");
		}
		Optional<IdKeyPair> optional = keyExchange.get(timeout, unit);
		if (!optional.isPresent()) {
			throw new RuntimeException("something happened");
		}
		IdKeyPair idKeyPair = optional.get();
		SessionPacketSenderImpl payloadSender = new SessionPacketSenderImpl(clientPayloadDatagramEndpoint);
		NetworkEndointForSessionConverter networkAccessPoint = new ClientNetworkEndpointForSessionConverter(payloadSender, serverHost, serverPort, clientPayloadDatagramEndpoint, idKeyPair.getId());
		sessionConverter = sessionConverterFactory.newSessionConverter(networkAccessPoint, idKeyPair);
		sessionInitialized = true;
		return sessionConverter;
	}
	
	public void send(Message message) throws IOException {
		if (!sessionInitialized) {
			throw new IllegalStateException("Session not yet initialized.");
		}
		sessionConverter.send(message);
	}

	public Message receive() throws Exception {
		return sessionConverter.receive();
	}

	@Override
	public void close() throws IOException {
		if (sessionConverter instanceof Closeable) {
			((Closeable) sessionConverter).close();
		}
	}

}
