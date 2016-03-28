package ch.bifrost.integrationtest.session;

import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import ch.bifrost.client.impl.session.ClientSessionEndpoint;
import ch.bifrost.client.impl.session.KeyExchangeClient;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import ch.bifrost.server.impl.session.EchoServer.EchoServerFactory;
import ch.bifrost.server.impl.session.KeyExchangeServer;
import ch.bifrost.server.impl.session.SessionInitializer;
import ch.bifrost.server.impl.session.SessionMultiplexConverter;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
public abstract class AbstractSessionTest {

    private static final String SERVER_HOST_NAME = "localhost";
	private static final int SERVER_PORT_KEY_EXCHANGE = 12345;
	private static final int SERVER_PORT_PAYLOAD = 12346;
	private SessionInitializer server;
	@Getter
	private ClientSessionEndpoint client;
	private UDPDatagramEndpoint serverKeyExchangeDatagramEndpoint;
	private UDPDatagramEndpoint serverPayloadDatagramEndpoint;
	private DatagramEndpoint clientPayloadDatagramEndpoint;
	private UDPDatagramEndpoint clientKeyExchangeDatagramEndpoint;

	@BeforeClass
	public static void setupLogger() {
		ConsoleAppender console = new ConsoleAppender();
		String pattern = "%d [%-5p|%c] %m%n";
		console.setLayout(new PatternLayout(pattern));
		console.setThreshold(Level.ALL);
		console.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(console);
	}
	
	@Before
	public void setupClientAndServer() throws Exception {
		initializeServer(getServerSessionConverterFactory());
		initializeClient(getClientSessionConverterFactory());
	}

	private void initializeServer(SessionConverterFactory sessionConverterFactory) throws SocketException {
		serverKeyExchangeDatagramEndpoint = new UDPDatagramEndpoint(SERVER_PORT_KEY_EXCHANGE);
		KeyExchangeServer keyExchangeServer = new KeyExchangeServer(serverKeyExchangeDatagramEndpoint);
		serverPayloadDatagramEndpoint = new UDPDatagramEndpoint(SERVER_PORT_PAYLOAD);
		SessionMultiplexConverter multiplexer = new SessionMultiplexConverter(serverPayloadDatagramEndpoint);
		server = new SessionInitializer(keyExchangeServer, multiplexer, sessionConverterFactory, new EchoServerFactory());
		server.start();
	}
	
	private void initializeClient(SessionConverterFactory sessionConverterFactory) throws Exception {
		InetAddress serverHost = InetAddress.getByName(SERVER_HOST_NAME);
		clientKeyExchangeDatagramEndpoint = new UDPDatagramEndpoint();
		KeyExchangeClient keyExchangeClient = new KeyExchangeClient(clientKeyExchangeDatagramEndpoint, serverHost, SERVER_PORT_KEY_EXCHANGE);
		clientPayloadDatagramEndpoint = new UDPDatagramEndpoint();
		client = new ClientSessionEndpoint(keyExchangeClient, sessionConverterFactory, clientPayloadDatagramEndpoint, serverHost, SERVER_PORT_PAYLOAD);
	}
	
	@After
	public void closeClientAndServer() throws Exception {
		client.close();
		clientKeyExchangeDatagramEndpoint.close();
		clientPayloadDatagramEndpoint.close();
		server.cancel();
		serverKeyExchangeDatagramEndpoint.close();
		serverPayloadDatagramEndpoint.close();
	}
	
	protected abstract SessionConverterFactory getServerSessionConverterFactory();

	protected abstract SessionConverterFactory getClientSessionConverterFactory();
	
}
