package ch.bifrost.server.impl.datagram;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.MultiplexingID;
import ch.bifrost.core.impl.datagram.DatagramMessageWithId;
import ch.bifrost.core.impl.datagram.DatagramMessageWithIdSender;
import ch.bifrost.core.impl.datagram.MultiplexedDatagramEndpoint;

/**
 * Represents the access point to the network for a server session adapter after multiplexing.
 */
public class ServerMultiplexedDatagramEndpoint extends MultiplexedDatagramEndpoint {

	private final BlockingQueue<DatagramMessageWithId> receivedMessages;

	public ServerMultiplexedDatagramEndpoint (DatagramMessageWithIdSender sessionPacketSender, BlockingQueue<DatagramMessageWithId> receivedMessages, MultiplexingID multiplexingId) {
		super(sessionPacketSender, multiplexingId);
		this.receivedMessages = receivedMessages;
	}

	@Override
	protected DatagramMessageWithId internalReceive () throws InterruptedException {
		DatagramMessageWithId sessionPacket = receivedMessages.take();
		return sessionPacket;
	}

	@Override
	protected Optional<DatagramMessageWithId> internalReceive (long timeout, TimeUnit unit) throws InterruptedException {
		DatagramMessageWithId packet = receivedMessages.poll(timeout, unit);
		if (packet == null) {
			return Optional.absent();
		}
		return Optional.of(packet);
	}

	@Override
	public void close () throws IOException {
		// nothing to do
	}

}
