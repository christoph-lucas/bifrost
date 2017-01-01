package ch.bifrost.integrationtest.session;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.Guice;

import ch.bifrost.client.impl.dependencyInjection.ClientServiceBinder;
import ch.bifrost.client.impl.session.SessionClient;
import ch.bifrost.core.api.config.BifrostConfiguration;
import ch.bifrost.server.impl.dependencyInjection.ServerServiceBinder;
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
	public static void setupLogger () {
		ConsoleAppender console = new ConsoleAppender();
		String pattern = "%d [%-5p|%c] %m%n";
		console.setLayout(new PatternLayout(pattern));
		console.setThreshold(Level.ALL);
		console.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(console);
	}

	@Before
	public void setupClientAndServer () throws Exception {
		server = Guice.createInjector(new ServerServiceBinder(getConfig())).getInstance(SessionServer.class);
		client = Guice.createInjector(new ClientServiceBinder(getConfig())).getInstance(SessionClient.class);
	}

	protected BifrostConfiguration getConfig () {
		BifrostConfiguration config = new BifrostConfiguration();
		config.getServer()
				.setServerHostName(SERVER_HOST_NAME)
				.setServerKeyExchangePort(SERVER_PORT_KEY_EXCHANGE)
				.setServerPayloadPort(SERVER_PORT_PAYLOAD);
		return config;
	}

	@After
	public void closeClientAndServer () throws Exception {
		client.close();
		server.close();
	}

}
