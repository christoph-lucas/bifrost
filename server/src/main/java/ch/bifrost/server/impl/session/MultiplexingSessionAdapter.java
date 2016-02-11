package ch.bifrost.server.impl.session;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramLayerAdapter;
import ch.bifrost.core.api.datagram.Packet;
import ch.bifrost.core.api.session.SessionLayerAdapter;
import ch.bifrost.core.api.session.SessionLayerAdapterFactory;
import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.api.session.SessionPacketSender;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import ch.bifrost.core.impl.session.SessionPacketSenderImpl;
import ch.bifrost.core.impl.session.SingleSessionEndpoint;
import ch.bifrost.server.api.server.ServerProcess;
import ch.bifrost.server.api.server.ServerProcessFactory;

/**
 * Multiplexes a single Datagram Endpoint into several sessions. Makes sure each message is sent to the correct process.
 */
public class MultiplexingSessionAdapter implements Closeable {

	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
	
	private DatagramLayerAdapter datagrams;
	private MultiplexingReceiver receiver;

	public MultiplexingSessionAdapter(int port, SessionLayerAdapterFactory sessionAdapterFactory, ServerProcessFactory serverFactory) throws SocketException {
		datagrams = new UDPDatagramEndpoint(port);
		receiver = new MultiplexingReceiver(datagrams, new SessionPacketSenderImpl(datagrams), sessionAdapterFactory, serverFactory);
		receiver.start();
	}
	
	@Override
	public void close() throws IOException {
		receiver.cancel();
		datagrams.close();
	}

	private class MultiplexingReceiver extends Thread {
		
		private static final int NUM_THREADS = 10;
		private DatagramLayerAdapter datagramEndpoint;
		private SessionStore sessionStore = new SessionStore();
		private boolean cancelled;
		private ExecutorService threadPool;
		private SessionPacketSender sessionPacketSender;
		private SessionLayerAdapterFactory sessionAdapterFactory;
		private ServerProcessFactory serverFactory;

		public MultiplexingReceiver(DatagramLayerAdapter datagramEndpoint, SessionPacketSender sessionPacketSender, SessionLayerAdapterFactory sessionAdapterFactory, ServerProcessFactory serverFactory) {
			this.datagramEndpoint = datagramEndpoint;
			this.sessionPacketSender = sessionPacketSender;
			this.sessionAdapterFactory = sessionAdapterFactory;
			this.serverFactory = serverFactory;
			threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		}
		
		@Override
		public void run() {
			while(!cancelled) {
				Optional<Packet> receivedPacket;
				try {
					receivedPacket = datagramEndpoint.receive(TIMEOUT, TIMEOUT_UNIT);
				} catch (InterruptedException e) {
					continue;
				}
				if (!receivedPacket.isPresent()) {
					continue;
				}
				SessionPacket sessionPacket = SessionPacket.fromPacket(receivedPacket.get());

				String id = sessionPacket.getSessionId();
				BlockingQueue<SessionPacket> queue;
				
				if (id == null || !sessionStore.contains(id)) {
					queue = new LinkedBlockingQueue<>();
					SingleSessionEndpoint singleSessionEndpoint = new SingleSessionEndpoint(sessionPacketSender, queue, sessionPacket.getCounterpartAddress(), sessionPacket.getCounterpartPort());
					SessionLayerAdapter sessionAdapter = sessionAdapterFactory.newSessionLayerAdapter(singleSessionEndpoint);
					String computedId = sessionAdapter.computeId(sessionPacket);
					ServerProcess newServerProcess = serverFactory.newServerProcess(sessionAdapter);
					sessionStore.put(computedId, queue, newServerProcess);
					// ugly: have to fix the ID in the Packet to make it be accepted
					sessionPacket = new SessionPacket(sessionPacket.getCounterpartAddress(), sessionPacket.getCounterpartPort(), computedId, sessionPacket.getContent());
					threadPool.submit(newServerProcess);
				} else {
					queue = sessionStore.get(id).getReceivedPackages();
				}
				try {
					queue.put(sessionPacket);
				} catch (InterruptedException e) {
					System.out.println("Cannot file SessionPacket: " + sessionPacket);
					e.printStackTrace();
				}
			}
		}
		
		public void cancel() {
			cancelled = true;
			sessionStore.killAll();
			try {
				Thread.sleep(TIMEOUT);
			} catch (InterruptedException e) {
				// ignore
			}
			threadPool.shutdownNow();
		}
		
	}

}
