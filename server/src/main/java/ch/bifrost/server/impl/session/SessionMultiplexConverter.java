package ch.bifrost.server.impl.session;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.Packet;
import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.core.api.session.SessionPacketSender;
import ch.bifrost.core.impl.session.NetworkEndointForSessionConverter;
import ch.bifrost.core.impl.session.SessionPacketSenderImpl;

/**
 * Multiplexes a single {@link DatagramEndpoint} into several {@link NetworkEndointForSessionConverter}. Makes sure each message is sent to the correct process.
 * 
 * Allows to register new Session IDs and provides a {@link NetworkEndointForSessionConverter} in return.
 */
public class SessionMultiplexConverter implements Closeable {

	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
	
	private final MultiplexingReceiver receiver;
	private final SessionStore sessionStore = new SessionStore();
	private final SessionPacketSender sessionPacketSender;
	

	public SessionMultiplexConverter(DatagramEndpoint datagramEndpoint) throws SocketException {
		sessionPacketSender = new SessionPacketSenderImpl(datagramEndpoint);
		receiver = new MultiplexingReceiver(datagramEndpoint);
		receiver.start();
	}
	
	@Override
	public void close() {
		receiver.cancel();
	}
	
	public NetworkEndointForSessionConverter registerSessionID(String id) {
		BlockingQueue<SessionPacket> queue = new LinkedBlockingQueue<>();
		NetworkEndointForSessionConverter singleSessionEndpoint = new ServerNetworkEndpointForSessionConverter(sessionPacketSender, queue, id);
		// should probably throw an exception if a session with that ID already exists
		sessionStore.put(id, queue);
		return singleSessionEndpoint;
	}
	
	public SessionState getSession(String id) {
		return sessionStore.get(id);
	}
	
	private class MultiplexingReceiver extends Thread {
		
	    private final Logger LOG = LoggerFactory.getLogger(MultiplexingReceiver.class);

		private DatagramEndpoint datagramEndpoint;
		private boolean cancelled;

		public MultiplexingReceiver(DatagramEndpoint datagramEndpoint) {
			this.datagramEndpoint = datagramEndpoint;
		}
		
		@Override
		public void run() {
			while(!cancelled) {
				Optional<Packet> receivedPacket = Optional.absent();
				try {
					receivedPacket = datagramEndpoint.receive(TIMEOUT, TIMEOUT_UNIT);
				} catch (IOException e) {
					continue;
				}
				if (!receivedPacket.isPresent()) {
					continue;
				}
				LOG.debug("Received a message: " + receivedPacket.get());
				SessionPacket sessionPacket = SessionPacket.fromPacket(receivedPacket.get());
				String id = sessionPacket.getSessionId();
				if (id == null || !sessionStore.contains(id)) {
					LOG.warn("Received unknown session ID: " + id);
					continue;
				}
				
				try {
					sessionStore.get(id).getReceivedPackages().put(sessionPacket);
				} catch (InterruptedException e) {
					LOG.debug("Cannot queue SessionPacket: " + sessionPacket);
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
		}
		
	}

}
