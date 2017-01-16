package ch.bifrost.server.impl.keyexchange;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.datagram.MultiplexingID;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.keyexchange.Key;
import ch.bifrost.core.impl.dependencyInjection.KeyExchange;

public class KeyExchangeServer implements Closeable {

	private DatagramEndpoint datagramEndpoint;

	@Inject
	public KeyExchangeServer (@KeyExchange DatagramEndpoint datagramEndpoint) {
		this.datagramEndpoint = datagramEndpoint;
	}

	public Optional<IdKeyPair> get (MultiplexingID id, long timeout, TimeUnit unit) throws Exception {
		Optional<DatagramMessage> receiveOpt = datagramEndpoint.receive(timeout, unit);
		if (!receiveOpt.isPresent()) {
			return Optional.absent();
		}
		DatagramMessage request = receiveOpt.get();

		IdKeyPair idKeyPair = new IdKeyPair(id, Key.createRandomKey());

		DatagramMessage response = new DatagramMessage(request.getCounterpartAddress(), idKeyPair.encode());
		datagramEndpoint.send(response);

		return Optional.of(idKeyPair);
	}

	@Override
	public void close () throws IOException {
		this.datagramEndpoint.close();
	}

}
