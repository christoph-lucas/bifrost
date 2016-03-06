package ch.bifrost.client.impl.session;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ch.bifrost.core.api.datagram.DatagramLayerAdapter;
import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionLayerAdapter;
import ch.bifrost.core.api.session.SessionLayerAdapterFactory;
import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import ch.bifrost.core.impl.session.SessionPacketSenderImpl;
import ch.bifrost.core.impl.session.SingleSessionEndpoint;

/**
 * Plugs everything together for a client.
 */
public class ClientSessionEndpoint<T extends SessionLayerAdapter> implements Closeable {

	public static final long TIMEOUT = 100L;

	private T sessionAdapter;
	private DatagramLayerAdapter datagramEndpoint;
	private SingleSessionReceiver receiver;

	public ClientSessionEndpoint(String serverHost, int serverPort, String sessionId, SessionLayerAdapterFactory<T> factory) throws UnknownHostException, SocketException {
		datagramEndpoint = new UDPDatagramEndpoint();
		BlockingQueue<SessionPacket> queue = new LinkedBlockingQueue<>();
		receiver = new SingleSessionReceiver(datagramEndpoint, queue);
		receiver.start();
		SingleSessionEndpoint singleSessionEndpoint = new SingleSessionEndpoint(new SessionPacketSenderImpl(datagramEndpoint), queue, InetAddress.getByName(serverHost), serverPort);
		sessionAdapter = factory.newSessionLayerAdapter(singleSessionEndpoint);
	}
	
	public void send(Message message) throws IOException {
		sessionAdapter.send(message);
	}

	public Message receive() throws InterruptedException {
		return sessionAdapter.receive();
	}

	@Override
	public void close() throws IOException {
		receiver.cancel();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			// ignore
		}
		datagramEndpoint.close();
	}

	public T getSessionLayerAdapter() {
		return sessionAdapter;
	}
}
