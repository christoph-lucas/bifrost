package ch.bifrost.integrationtest.session;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.inject.Guice;

import ch.bifrost.client.impl.dependencyInjection.ClientServiceBinder;
import ch.bifrost.client.impl.keyexchange.KeyExchangeClient;
import ch.bifrost.core.api.config.BifrostConfiguration;
import ch.bifrost.core.api.datagram.MultiplexingID;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.impl.MessageCodecUtils;
import ch.bifrost.server.impl.dependencyInjection.ServerServiceBinder;
import ch.bifrost.server.impl.keyexchange.KeyExchangeServer;
import lombok.Getter;

public class KeyExchangeTest {

	private static final String SERVER_HOST_NAME = "localhost";
	private static final int SERVER_PORT_KEY_EXCHANGE = 4567;
	// ID must be 32 bytes
	private static final MultiplexingID ID = MultiplexingID.fromBytes(
			MessageCodecUtils.encodeStringAsByteArrayWithFixedLength("01234567890123456789012345678912", MultiplexingID.LENGTH_IN_BYTES));

	@BeforeClass
	public static void setupLogger () throws Exception {
		ConsoleAppender console = new ConsoleAppender();
		String pattern = "%d [%-5p|%c] %m%n";
		console.setLayout(new PatternLayout(pattern));
		console.setThreshold(Level.ALL);
		console.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(console);
	}

	@Test
	public void shouldExchangeAKey () throws Exception {
		try (
				KeyExchangeServer server = Guice.createInjector(new ServerServiceBinder(getConfig())).getInstance(KeyExchangeServer.class);
				KeyExchangeClient client = Guice.createInjector(new ClientServiceBinder(getConfig())).getInstance(KeyExchangeClient.class)
		) {
			ServerThread serverThread = new ServerThread(server);

			serverThread.start();
			Optional<IdKeyPair> clientKey = client.get(1000, TimeUnit.SECONDS);
			serverThread.join();

			assertTrue(clientKey.isPresent());
			assertThat(clientKey.get().getId(), is(equalTo(ID)));
			assertTrue(serverThread.getServerKey().isPresent());
			assertThat(serverThread.getServerKey().get().getId(), is(equalTo(ID)));
			assertThat(serverThread.getServerKey().get().getKey(), is(equalTo(clientKey.get().getKey())));
		}
	}

	protected BifrostConfiguration getConfig () {
		BifrostConfiguration config = new BifrostConfiguration();
		config.getServer()
				.setServerHostName(SERVER_HOST_NAME)
				.setServerKeyExchangePort(SERVER_PORT_KEY_EXCHANGE);
		return config;
	}

	private static class ServerThread extends Thread {

		private KeyExchangeServer server;
		@Getter
		private Optional<IdKeyPair> serverKey;

		public ServerThread (KeyExchangeServer server) {
			this.server = server;
		}

		@Override
		public void run () {
			try {
				serverKey = server.get(ID, 1000, TimeUnit.SECONDS);
			} catch (Exception e) {
				// ignore
			}
		}
	}

}
