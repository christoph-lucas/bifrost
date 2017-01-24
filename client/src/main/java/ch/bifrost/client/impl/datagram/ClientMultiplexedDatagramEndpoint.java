package ch.bifrost.client.impl.datagram;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.datagram.MultiplexingID;
import ch.bifrost.core.impl.datagram.DatagramMessageWithId;
import ch.bifrost.core.impl.datagram.DatagramMessageWithIdSender;
import ch.bifrost.core.impl.datagram.InvalidDatagramException;
import ch.bifrost.core.impl.datagram.MultiplexedDatagramEndpoint;
import ch.bifrost.core.impl.dependencyInjection.Payload;

/**
 * This is a simple wrapper around a DatagramEndpoint. It interpretes the first bytes of the payload
 * as multiplexing ID and cuts them off.
 */
public class ClientMultiplexedDatagramEndpoint extends MultiplexedDatagramEndpoint {

	private final Logger LOG = LoggerFactory.getLogger(ClientMultiplexedDatagramEndpoint.class);

	private final DatagramEndpoint datagramEndpoint;
	private final MultiplexingID multiplexingId;

	@Inject
	public ClientMultiplexedDatagramEndpoint (@Payload CounterpartAddress serverAddress, 
			@Payload DatagramEndpoint datagramEndpoint,
			@Assisted MultiplexingID multiplexingId) {
		super(new DatagramMessageWithIdSender(datagramEndpoint, multiplexingId));
		this.multiplexingId = multiplexingId;
		super.counterpartAddress(serverAddress);
		this.datagramEndpoint = datagramEndpoint;
	}

	@Override
	protected DatagramMessage internalReceive () throws IOException, InterruptedException, InvalidDatagramException {
		while (true) {
			DatagramMessage receivedPacket = datagramEndpoint.receive();
			DatagramMessageWithId msgWithId = DatagramMessageWithId.from(receivedPacket);
			if (idMatches(msgWithId)) {
				return msgWithId.getPlainDatagram();
			}
			LOG.warn("Received unknown multiplexing ID: " + msgWithId.getMultiplexingId());
		}
	}

	@Override
	protected Optional<DatagramMessage> internalReceive (long timeout, TimeUnit unit) throws IOException, InterruptedException, InvalidDatagramException {
		Optional<DatagramMessage> receivedPacket = datagramEndpoint.receive(timeout, unit);
		if (receivedPacket.isPresent()) {
			DatagramMessageWithId datagramWithId = DatagramMessageWithId.from(receivedPacket.get());
			if (idMatches(datagramWithId)) {
				return Optional.of(datagramWithId.getPlainDatagram());
			}
			LOG.warn("Received unknown multiplexing ID: " + datagramWithId.getMultiplexingId());
		}
		return Optional.absent();
	}

	private boolean idMatches (DatagramMessageWithId datagramWithId) {
		return this.multiplexingId.equals(datagramWithId.getMultiplexingId());
	}

	@Override
	public void close () throws IOException {
		this.datagramEndpoint.close();
	}
	
	public static interface ClientMultiplexedDatagramEndpointFactory {
		ClientMultiplexedDatagramEndpoint create(MultiplexingID multiplexingId);
	}
	
}
