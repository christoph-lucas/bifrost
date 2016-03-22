package ch.bifrost.integrationtest.session;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.client.impl.session.ClientSessionEndpoint;
import ch.bifrost.client.impl.session.DefaultSessionLayerClientAdapter;
import ch.bifrost.client.impl.session.DefaultSessionLayerClientAdapter.DefaultSessionLayerClientAdapterFactory;
import ch.bifrost.core.api.session.Message;
import ch.bifrost.server.impl.session.DefaultSessionLayerServerAdapter.DefaultSessionLayerServerAdapterFactory;
import ch.bifrost.server.impl.session.EchoServer.EchoServerFactory;
import ch.bifrost.server.impl.session.DefaultSessionLayerServerAdapter;
import ch.bifrost.server.impl.session.MultiplexingSessionAdapter;

public class DefaultSessionTest {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionTest.class);
	
	private static final int SERVER_PORT = 12345;
	private MultiplexingSessionAdapter<DefaultSessionLayerServerAdapter> server;
	private ClientSessionEndpoint<DefaultSessionLayerClientAdapter> client;

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
		server = new MultiplexingSessionAdapter<DefaultSessionLayerServerAdapter>(SERVER_PORT, new DefaultSessionLayerServerAdapterFactory(), new EchoServerFactory());
		client = new ClientSessionEndpoint<DefaultSessionLayerClientAdapter>("localhost", SERVER_PORT, "123", new DefaultSessionLayerClientAdapterFactory());
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
	
	@Test
	public void shouldPingAndRekey() throws Exception {
		client.send(new Message("ping"));
		LOG.info(client.receive().toString());
		client.getSessionLayerAdapter().rekey();
	}

}
