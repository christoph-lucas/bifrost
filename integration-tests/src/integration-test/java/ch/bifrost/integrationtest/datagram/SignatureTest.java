package ch.bifrost.integrationtest.datagram;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;

import ch.bifrost.client.impl.keyexchange.DatagramVerifyingConverter;
import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.impl.MessageCodecUtils;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import ch.bifrost.server.impl.keyexchange.DatagramSigningConverter;
import lombok.Getter;

public class SignatureTest {

	private static final int SERVER_PORT_KEY_EXCHANGE = 12345;

	private static final String CLIENT_HOST_NAME = "127.0.0.1";
	private static final int CLIENT_PORT = 4567;
	private static final String SERVER_KEYSTORE = "../ca/server.keystore";
	private static final String SERVER_KEYSTORE_PASSWORD = "bifrost";
	private static final String SERVER_KEY_ALIAS = "1";
	private static final String CLIENT_KEYSTORE = "../ca/client.keystore";
	private static final String TEST_STRING = "Hello world!";
	// ID must be 32 bytes

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
	public void shouldTransmitAMessage () throws Exception {
		// Read keys from server keystore
		FileInputStream sis = new FileInputStream(SERVER_KEYSTORE);
		KeyStore serverKeys = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] passwd = SERVER_KEYSTORE_PASSWORD.toCharArray();
		serverKeys.load(sis, passwd);
		sis.close();
		PrivateKey privateKey = (PrivateKey) serverKeys.getKey(SERVER_KEY_ALIAS, passwd);
		Certificate[] certChain = serverKeys.getCertificateChain(SERVER_KEY_ALIAS);

		// Set up the network sockets
		UDPDatagramEndpoint serverUDP = new UDPDatagramEndpoint(SERVER_PORT_KEY_EXCHANGE);
		DatagramSigningConverter serverConv = new DatagramSigningConverter(serverUDP, certChain, privateKey);
		serverConv.counterpartAddress(new CounterpartAddress(CLIENT_HOST_NAME, CLIENT_PORT));

		// Read client keystore
		FileInputStream cis = new FileInputStream(CLIENT_KEYSTORE);
		KeyStore clientKeys = KeyStore.getInstance(KeyStore.getDefaultType());
		clientKeys.load(cis, passwd);
		cis.close();

		ClientThread ct = new ClientThread(clientKeys);
		ct.start();

		// This is just to make sure the thread had enough time to start
		Thread.sleep(500);

		// Send message
		DatagramMessage msg = new DatagramMessage(new CounterpartAddress(CLIENT_HOST_NAME, CLIENT_PORT), MessageCodecUtils.encodeStringAsByteArray(TEST_STRING));
		serverConv.send(msg);

		// This is to make sure the message had enough time to arrive
		ct.join();

		assertTrue(ct.getMessage().isPresent());
		assertThat(ct.getMessage().get(), is(equalTo(TEST_STRING)));

		// Shouldn't we also test for broken messages failing? To make sure that we got the checks right?

		serverConv.close();
		serverUDP.close();
	}

	private class ClientThread extends Thread {

		@Getter
		private Optional<String> message;
		UDPDatagramEndpoint clientUDP;
		DatagramVerifyingConverter client;

		@Override
		public void run () {
			try {
				DatagramMessage recv = client.receive();
				message = Optional.of(MessageCodecUtils.decodeStringFromByteArray(recv.getPayload()));
			} catch (Exception e) {
				// ignore
			}
			// Close all the stuff
			try {
				if (client != null) {
					client.close();
				}
				if (clientUDP != null) {
					clientUDP.close();
				}
			} catch (Exception e) {
			}
		}

		public ClientThread (KeyStore clientKeys) {
			try {
				clientUDP = new UDPDatagramEndpoint(CLIENT_PORT);
				client = new DatagramVerifyingConverter(clientUDP, clientKeys);
			} catch (Exception e) {
			}
		}
	}

}
