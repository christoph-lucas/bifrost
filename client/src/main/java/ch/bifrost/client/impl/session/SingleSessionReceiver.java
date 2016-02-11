package ch.bifrost.client.impl.session;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramLayerAdapter;
import ch.bifrost.core.api.datagram.Packet;
import ch.bifrost.core.api.session.SessionException;
import ch.bifrost.core.api.session.SessionPacket;

public class SingleSessionReceiver extends Thread {
	
	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	private DatagramLayerAdapter datagramEndpoint;
	private BlockingQueue<SessionPacket> queue;
	private boolean cancelled;

	public SingleSessionReceiver(DatagramLayerAdapter datagramEndpoint, BlockingQueue<SessionPacket> queue) {
		this.datagramEndpoint = datagramEndpoint;
		this.queue = queue;
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

			try {
				queue.put(sessionPacket);
			} catch (InterruptedException e) {
				throw new SessionException("Cannot put message", e);
			}
		}
	}
	
	public void cancel() {
		cancelled = true;
	}
	
}
