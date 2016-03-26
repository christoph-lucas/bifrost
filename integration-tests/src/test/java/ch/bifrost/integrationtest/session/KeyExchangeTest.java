package ch.bifrost.integrationtest.session;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import ch.bifrost.client.impl.session.KeyExchangeClient;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.session.IdKeyPair;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import ch.bifrost.server.impl.session.KeyExchangeServer;

public class KeyExchangeTest {

    private static final Logger LOG = LoggerFactory.getLogger(KeyExchangeTest.class);
	private static InetAddress SERVER_HOST;
	private static final int SERVER_PORT = 4567;

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
		new ServerThread(server).start();
		
		Optional<IdKeyPair> optionalKey = client.get(1000, TimeUnit.SECONDS);
		if (optionalKey.isPresent()) {
			LOG.info("Client: " + optionalKey.get().toString());
		} else {
			LOG.info("Got no IdKeyPair");
		}
		
	}
	
	
	private static class ServerThread extends Thread {
		
	    private static final Logger LOG = LoggerFactory.getLogger(ServerThread.class);
		private KeyExchangeServer server;

		public ServerThread(KeyExchangeServer server) {
			this.server = server;
		}
		
		@Override
		public void run() {
			try {
				Optional<IdKeyPair> optionalKey = server.get(1000, TimeUnit.SECONDS);
				if (optionalKey.isPresent()) {
					LOG.info("Server: " + optionalKey.get().toString());
				} else {
					LOG.info("Server got no IdKeyPair");
				}
			} catch (Exception e) {
				LOG.error("something went wrong", e);
			}
		}
	}
	
	
}
