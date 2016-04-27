package ch.bifrost.integrationtest.session;

import java.net.InetAddress;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import ch.bifrost.client.impl.session.SessionClient;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.server.impl.session.SessionServer;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
public abstract class AbstractSessionTest {

    private static final String SERVER_HOST_NAME = "localhost";
	private static final int SERVER_PORT_KEY_EXCHANGE = 12345;
	private static final int SERVER_PORT_PAYLOAD = 12346;
	@Getter
	private SessionClient client;
	private SessionServer server;
	
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
		server = new SessionServer(SERVER_PORT_KEY_EXCHANGE, SERVER_PORT_PAYLOAD, getServerSessionConverterFactory());
		InetAddress serverHost = InetAddress.getByName(SERVER_HOST_NAME);
		client = new SessionClient(serverHost, SERVER_PORT_KEY_EXCHANGE, SERVER_PORT_PAYLOAD, getClientSessionConverterFactory());
	}

	
	@After
	public void closeClientAndServer() throws Exception {
		client.close();
		server.close();
	}
	
	protected abstract SessionConverterFactory getServerSessionConverterFactory();

	protected abstract SessionConverterFactory getClientSessionConverterFactory();
	
}
