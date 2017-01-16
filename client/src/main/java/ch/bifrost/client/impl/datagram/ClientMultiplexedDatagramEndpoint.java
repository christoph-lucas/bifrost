package ch.bifrost.client.impl.datagram;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.datagram.MultiplexingID;
import ch.bifrost.core.impl.datagram.DatagramMessageWithId;
import ch.bifrost.core.impl.datagram.DatagramMessageWithIdSender;
import ch.bifrost.core.impl.datagram.MultiplexedDatagramEndpoint;
import ch.bifrost.core.impl.dependencyInjection.Payload;

/**
 * This is a simple wrapper around a DatagramEndpoint. It interpretes the first bytes of the payload
 * as multiplexing ID and cuts them off.
 */
public class ClientMultiplexedDatagramEndpoint extends MultiplexedDatagramEndpoint {

	private DatagramEndpoint datagramEndpoint;

	@Inject
	public ClientMultiplexedDatagramEndpoint (@Payload CounterpartAddress serverAddress, 
			@Payload DatagramEndpoint datagramEndpoint,
			@Assisted MultiplexingID multiplexingId) {
		super(new DatagramMessageWithIdSender(datagramEndpoint), multiplexingId);
		super.counterpartAddress(serverAddress);
		this.datagramEndpoint = datagramEndpoint;
	}

	@Override
	protected DatagramMessageWithId internalReceive () throws IOException, InterruptedException {
		DatagramMessage receivedPacket = datagramEndpoint.receive();
		return DatagramMessageWithId.from(receivedPacket);
	}

	@Override
	protected Optional<DatagramMessageWithId> internalReceive (long timeout, TimeUnit unit) throws IOException, InterruptedException {
		Optional<DatagramMessage> receivedPacket = datagramEndpoint.receive(timeout, unit);
		if (receivedPacket.isPresent()) {
			return Optional.of(DatagramMessageWithId.from(receivedPacket.get()));
		}
		return Optional.absent();
	}

	@Override
	public void close () throws IOException {
		this.datagramEndpoint.close();
	}
	
	public static interface ClientMultiplexedDatagramEndpointFactory {
		ClientMultiplexedDatagramEndpoint create(MultiplexingID multiplexingId);
	}
	
}
