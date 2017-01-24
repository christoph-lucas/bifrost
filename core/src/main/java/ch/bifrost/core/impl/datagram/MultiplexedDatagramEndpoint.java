package ch.bifrost.core.impl.datagram;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class MultiplexedDatagramEndpoint implements DatagramEndpoint {

	private final DatagramMessageWithIdSender datagramWithIdSender;
	@Setter
	private CounterpartAddress counterpartAddress;

	public MultiplexedDatagramEndpoint (DatagramMessageWithIdSender datagramWithIdSender) {
		this.datagramWithIdSender = datagramWithIdSender;
	}

	protected abstract DatagramMessage internalReceive () throws IOException, InterruptedException, InvalidDatagramException;

	protected abstract Optional<DatagramMessage> internalReceive (long timeout, TimeUnit unit) throws IOException, InterruptedException, InvalidDatagramException;

	@Override
	public void send (DatagramMessage message) throws IOException {
		DatagramMessage datagram;
		if (counterpartAddress != null && counterpartAddress.isValid()) {
			datagram = new DatagramMessage(counterpartAddress, message.getPayload());
		} else {
			datagram = new DatagramMessage(message.getCounterpartAddress(), message.getPayload());
		}
		datagramWithIdSender.send(datagram);
	}

	@Override
	public DatagramMessage receive () throws IOException, InterruptedException, InvalidDatagramException {
		return internalReceive();
	}

	@Override
	public Optional<DatagramMessage> receive (long timeout, TimeUnit unit) throws IOException, InterruptedException, InvalidDatagramException {
		return internalReceive(timeout, unit);
	}

}