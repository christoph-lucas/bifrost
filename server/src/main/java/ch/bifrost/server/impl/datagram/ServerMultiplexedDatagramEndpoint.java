package ch.bifrost.server.impl.datagram;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.impl.datagram.DatagramMessageWithIdSender;
import ch.bifrost.core.impl.datagram.MultiplexedDatagramEndpoint;

/**
 * Represents the access point to the network for a server session adapter after multiplexing.
 */
public class ServerMultiplexedDatagramEndpoint extends MultiplexedDatagramEndpoint {

	private final BlockingQueue<DatagramMessage> receivedMessages;

	public ServerMultiplexedDatagramEndpoint (DatagramMessageWithIdSender sessionPacketSender, BlockingQueue<DatagramMessage> receivedMessages) {
		super(sessionPacketSender);
		this.receivedMessages = receivedMessages;
	}

	@Override
	protected DatagramMessage internalReceive () throws InterruptedException {
		return receivedMessages.take();
	}

	@Override
	protected Optional<DatagramMessage> internalReceive (long timeout, TimeUnit unit) throws InterruptedException {
		DatagramMessage packet = receivedMessages.poll(timeout, unit);
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
