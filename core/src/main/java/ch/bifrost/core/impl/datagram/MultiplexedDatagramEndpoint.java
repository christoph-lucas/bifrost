package ch.bifrost.core.impl.datagram;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.session.MultiplexingID;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class MultiplexedDatagramEndpoint implements DatagramEndpoint {

	private final DatagramMessageWithIdSender sessionPacketSender;
	private final MultiplexingID multiplexingId;
	@Setter
	private CounterpartAddress counterpartAddress;

	public MultiplexedDatagramEndpoint (DatagramMessageWithIdSender sessionPacketSender, MultiplexingID multiplexingId) {
		this.sessionPacketSender = sessionPacketSender;
		this.multiplexingId = multiplexingId;
	}

	protected abstract DatagramMessageWithId internalReceive () throws IOException, InterruptedException;

	protected abstract Optional<DatagramMessageWithId> internalReceive (long timeout, TimeUnit unit) throws IOException, InterruptedException;

	@Override
	public void send (DatagramMessage message) throws IOException {
		DatagramMessageWithId datagramWithId;
		if (counterpartAddress != null && counterpartAddress.isValid()) {
			datagramWithId = new DatagramMessageWithId(counterpartAddress, message.getPayload(), multiplexingId);
		} else {
			datagramWithId = new DatagramMessageWithId(message.getCounterpartAddress(), message.getPayload(), multiplexingId);
		}
		sessionPacketSender.send(datagramWithId);
	}

	@Override
	public DatagramMessage receive () throws IOException, InterruptedException {
		while (true) { // run until we have a valid message
			DatagramMessageWithId datagramMessageWithId = internalReceive();
			if (multiplexingId.equals(datagramMessageWithId.getMultiplexingId())) {
				return new DatagramMessage(
						datagramMessageWithId.getCounterpartAddress(),
						datagramMessageWithId.getPayload());
			}
		}
	}

	@Override
	public Optional<DatagramMessage> receive (long timeout, TimeUnit unit) throws IOException, InterruptedException {
		Optional<DatagramMessageWithId> datagramMessageWithId = internalReceive(timeout, unit);
		if (!datagramMessageWithId.isPresent()) {
			return Optional.absent();
		}
		if (multiplexingId.equals(datagramMessageWithId.get().getMultiplexingId())) {
			return Optional.of(new DatagramMessage(
					datagramMessageWithId.get().getCounterpartAddress(),
					datagramMessageWithId.get().getPayload()));
		}
		// received a message with wrong counterpart, return even if the timeout is not yet over
		return Optional.absent();
	}

}