package ch.bifrost.client.impl.keyexchange;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.impl.MessageCodecUtils;
import ch.bifrost.core.impl.dependencyInjection.KeyExchange;

public class KeyExchangeClient implements Closeable {

	private DatagramEndpoint datagramEndpoint;
	private CounterpartAddress serverAddress;

	@Inject
	public KeyExchangeClient (@KeyExchange DatagramEndpoint datagramEndpoint, @KeyExchange CounterpartAddress serverAddress) {
		this.datagramEndpoint = datagramEndpoint;
		this.serverAddress = serverAddress;
	}

	public Optional<IdKeyPair> get (long timeout, TimeUnit unit) throws Exception {
		byte[] messageBytes = MessageCodecUtils.encodeStringAsByteArray("Key Exchange Request");
		DatagramMessage request = new DatagramMessage(serverAddress, messageBytes);
		datagramEndpoint.send(request);
		Optional<DatagramMessage> response = datagramEndpoint.receive(timeout, unit);
		if (response.isPresent()) {
			return Optional.of(IdKeyPair.decode(response.get().getPayload()));
		}
		return Optional.absent();
	}

	@Override
	public void close () throws IOException {
		this.datagramEndpoint.close();
	}

}
