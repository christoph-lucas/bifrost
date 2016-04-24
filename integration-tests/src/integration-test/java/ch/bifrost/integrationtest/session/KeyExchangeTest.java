package ch.bifrost.integrationtest.session;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;

import ch.bifrost.client.impl.session.KeyExchangeClient;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.session.IdKeyPair;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import ch.bifrost.server.impl.session.KeyExchangeServer;
import lombok.Getter;

public class KeyExchangeTest {

	private static InetAddress SERVER_HOST;
	private static final int SERVER_PORT = 4567;
	// ID must be 32 bytes
	private static final String ID = "01234567890123456789012345678912";

	@BeforeClass
	public static void setupLogger() throws Exception {
		ConsoleAppender console = new ConsoleAppender();
		String pattern = "%d [%-5p|%c] %m%n";
		console.setLayout(new PatternLayout(pattern));
		console.setThreshold(Level.ALL);
		console.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(console);
		SERVER_HOST = InetAddress.getByName("localhost");
	}
	
	@Test
	public void shouldExchangeAKey() throws Exception {
		DatagramEndpoint serverEndpoint = new UDPDatagramEndpoint(SERVER_PORT);
		KeyExchangeServer server = new KeyExchangeServer(serverEndpoint);
		
		DatagramEndpoint clientEndpoint = new UDPDatagramEndpoint();
		KeyExchangeClient client = new KeyExchangeClient(clientEndpoint, SERVER_HOST, SERVER_PORT);
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
	
	
	private static class ServerThread extends Thread {
		
		private KeyExchangeServer server;
		@Getter
		private Optional<IdKeyPair> serverKey;

		public ServerThread(KeyExchangeServer server) {
			this.server = server;
		}
		
		@Override
		public void run() {
			try {
				serverKey = server.get(ID, 1000, TimeUnit.SECONDS);
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	
}
