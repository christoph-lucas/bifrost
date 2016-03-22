package ch.bifrost.integrationtest.session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.client.impl.session.ClientSessionEndpoint;
import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.impl.session.NoCryptoSessionLayerAdapter.NoCryptoSessionAdapterFactory;
import ch.bifrost.server.impl.session.EchoServer.EchoServerFactory;
import ch.bifrost.server.impl.session.MultiplexingSessionAdapter;

public class NoCryptoSessionTest {

    private static final Logger LOG = LoggerFactory.getLogger(NoCryptoSessionTest.class);
	
	private static final int SERVER_PORT = 12345;
	private MultiplexingSessionAdapter server;
	private ClientSessionEndpoint client;

	@Before
	public void setupClientAndServer() throws Exception {
		NoCryptoSessionAdapterFactory sessionAdapterFactory = new NoCryptoSessionAdapterFactory();
		server = new MultiplexingSessionAdapter(SERVER_PORT, sessionAdapterFactory, new EchoServerFactory());
		client = new ClientSessionEndpoint("localhost", SERVER_PORT, "123", sessionAdapterFactory);

	}
	
	@After
	public void closeClientAndServer() throws Exception {
		client.close();
		server.close();
	}
	
	@Test
	public void shouldPlayPingPongOnce() throws Exception {
		client.send(new Message("ping"));
		LOG.info(client.receive().toString());
	}
	
	@Test
	public void shouldPlayPingPongTwice() throws Exception {
		client.send(new Message("ping"));
		LOG.info(client.receive().toString());
		client.send(new Message("pong"));
		LOG.info(client.receive().toString());
	}
	
}
