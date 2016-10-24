package ch.bifrost.client.impl.datagram;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.session.MultiplexingID;
import ch.bifrost.core.impl.datagram.DatagramMessageWithId;
import ch.bifrost.core.impl.datagram.DatagramMessageWithIdSender;
import ch.bifrost.core.impl.datagram.MultiplexedDatagramEndpoint;

/**
 * This is a simple wrapper around a DatagramEndpoint. It interpretes the first bytes of the payload
 * as multiplexing ID and cuts them off.
 */
public class ClientMultiplexedDatagramEndpoint extends MultiplexedDatagramEndpoint {

	private DatagramEndpoint datagramEndpoint;

	public ClientMultiplexedDatagramEndpoint (CounterpartAddress serverAddress, DatagramEndpoint datagramEndpoint, MultiplexingID multiplexingId) {
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

}
