package ch.bifrost.server.impl.datagram;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.datagram.MultiplexingID;
import ch.bifrost.core.impl.datagram.DatagramMessageWithId;
import ch.bifrost.core.impl.datagram.DatagramMessageWithIdSender;
import ch.bifrost.core.impl.datagram.MultiplexedDatagramEndpoint;
import ch.bifrost.core.impl.dependencyInjection.Payload;

/**
 * Multiplexes a single {@link DatagramEndpoint} into several {@link MultiplexedDatagramEndpoint}. Makes sure each message is sent to the correct process.
 * 
 * Allows to register new Multiplexing IDs and provides a {@link MultiplexedDatagramEndpoint} in return.
 */
public class DatagramEndpointMultiplexer implements Closeable {

	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	private final MultiplexingReceiver receiver;
	private final DatagramMessageWithIdSender sessionPacketSender;
	private final Map<MultiplexingID, BlockingQueue<DatagramMessageWithId>> endpoints = new HashMap<>();
	private DatagramEndpoint datagramEndpoint;

	@Inject
	public DatagramEndpointMultiplexer (@Payload DatagramEndpoint datagramEndpoint) throws SocketException {
		this.datagramEndpoint = datagramEndpoint;
		sessionPacketSender = new DatagramMessageWithIdSender(datagramEndpoint);
		receiver = new MultiplexingReceiver(datagramEndpoint);
		receiver.start();
	}

	@Override
	public void close () throws IOException {
		receiver.cancel();
		datagramEndpoint.close();
	}

	public MultiplexedDatagramEndpoint registerSessionID (MultiplexingID id) {
		if (endpoints.containsKey(id)) {
			throw new DuplicateMultiplexingIdException("The ID " + id + " is already registered.");
		}
		BlockingQueue<DatagramMessageWithId> queue = new LinkedBlockingQueue<>();
		MultiplexedDatagramEndpoint singleSessionEndpoint = new ServerMultiplexedDatagramEndpoint(sessionPacketSender, queue, id);
		endpoints.put(id, queue);
		return singleSessionEndpoint;
	}

	private class MultiplexingReceiver extends Thread {

		private final Logger LOG = LoggerFactory.getLogger(MultiplexingReceiver.class);

		private DatagramEndpoint datagramEndpoint;
		private boolean cancelled;

		public MultiplexingReceiver (DatagramEndpoint datagramEndpoint) {
			this.datagramEndpoint = datagramEndpoint;
		}

		@Override
		public void run () {
			while (!cancelled) {
				Optional<DatagramMessage> datagram = Optional.absent();
				try {
					datagram = datagramEndpoint.receive(TIMEOUT, TIMEOUT_UNIT);
				} catch (IOException | InterruptedException e) {
					continue;
				}
				if (!datagram.isPresent()) {
					continue;
				}
				LOG.debug("Received a message: " + datagram.get());
				DatagramMessageWithId datagramMessageWithId = DatagramMessageWithId.from(datagram.get());
				MultiplexingID id = datagramMessageWithId.getMultiplexingId();
				if (id == null || !endpoints.containsKey(id)) {
					LOG.warn("Received unknown multiplexing ID: " + id);
					continue;
				}

				try {
					endpoints.get(id).put(datagramMessageWithId);
				} catch (InterruptedException e) {
					LOG.debug("Cannot queue DatagramMessageWithId: " + e.getMessage(), e);
					e.printStackTrace();
				}
			}
		}

		public void cancel () {
			cancelled = true;
			try {
				Thread.sleep(TIMEOUT);
			} catch (InterruptedException e) {
				// ignore
			}
		}

	}

	@SuppressWarnings("serial")
	public static class DuplicateMultiplexingIdException extends RuntimeException {

		public DuplicateMultiplexingIdException (String msg) {
			super(msg);
		}

	}

}
