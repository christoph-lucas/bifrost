package ch.bifrost.server.impl.keyexchange;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.session.MultiplexingID;

public class KeyExchangeServer {

	private static final int KEY_LENGTH_IN_BYTES = 64;

	private DatagramEndpoint datagramEndpoint;

	public KeyExchangeServer (DatagramEndpoint datagramEndpoint) {
		this.datagramEndpoint = datagramEndpoint;
	}

	public Optional<IdKeyPair> get (MultiplexingID id, long timeout, TimeUnit unit) throws Exception {
		Optional<DatagramMessage> receiveOpt = datagramEndpoint.receive(timeout, unit);
		if (!receiveOpt.isPresent()) {
			return Optional.absent();
		}
		DatagramMessage request = receiveOpt.get();

		byte[] key = new byte[KEY_LENGTH_IN_BYTES];
		ThreadLocalRandom.current().nextBytes(key);

		IdKeyPair idKeyPair = new IdKeyPair(id, key);

		DatagramMessage response = new DatagramMessage(request.getCounterpartAddress(), idKeyPair.encode());
		datagramEndpoint.send(response);

		return Optional.of(idKeyPair);
	}

}
