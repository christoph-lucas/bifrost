package ch.bifrost.client.impl.keyexchange;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.keyexchange.DHKeyExchange;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.keyexchange.Key;
import ch.bifrost.core.api.keyexchange.DHKeyExchangeRequestMessage;
import ch.bifrost.core.api.keyexchange.DHKeyExchangeResponseMessage;

public class DHKeyExchangeClient implements KeyExchangeClient {

	private DatagramEndpoint datagramEndpoint;
	private CounterpartAddress serverAddress;

	public DHKeyExchangeClient (DatagramEndpoint datagramEndpoint, CounterpartAddress serverAddress) {
		this.datagramEndpoint = datagramEndpoint;
		this.serverAddress = serverAddress;
	}

	@Override
	public Optional<IdKeyPair> get (long timeout, TimeUnit unit) throws Exception {
		DHKeyExchange dh = new DHKeyExchange();
		DHKeyExchangeRequestMessage keyRequest = new DHKeyExchangeRequestMessage(dh.getPublicKey());

		DatagramMessage request = new DatagramMessage(serverAddress, keyRequest.toByteArray());
		datagramEndpoint.send(request);

		Optional<DatagramMessage> response = datagramEndpoint.receive(timeout, unit);
		if (!response.isPresent()) {
			return Optional.absent();
		}
		DHKeyExchangeResponseMessage keyResponse = DHKeyExchangeResponseMessage.from(response.get().getPayload());

		BigInteger sharedKey = dh.getSharedKey(keyResponse.getServerPublicKey());
		Key key = Key.fromBytes(sharedKey.toByteArray());

		return Optional.of(new IdKeyPair(keyResponse.getId(), key));
	}

}
