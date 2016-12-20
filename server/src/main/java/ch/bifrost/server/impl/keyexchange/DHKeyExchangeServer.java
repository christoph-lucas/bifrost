package ch.bifrost.server.impl.keyexchange;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.keyexchange.DHKeyExchange;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.keyexchange.Key;
import ch.bifrost.core.api.keyexchange.DHKeyExchangeRequestMessage;
import ch.bifrost.core.api.keyexchange.DHKeyExchangeResponseMessage;
import ch.bifrost.core.api.session.MultiplexingID;

public class DHKeyExchangeServer implements KeyExchangeServer {

	private DatagramEndpoint datagramEndpoint;

	public DHKeyExchangeServer (DatagramEndpoint datagramEndpoint) {
		this.datagramEndpoint = datagramEndpoint;
	}

	@Override
	public Optional<IdKeyPair> get (MultiplexingID id, long timeout, TimeUnit unit) throws Exception {
		Optional<DatagramMessage> receiveOpt = datagramEndpoint.receive(timeout, unit);
		if (!receiveOpt.isPresent()) {
			return Optional.absent();
		}
		DatagramMessage request = receiveOpt.get();

		DHKeyExchangeRequestMessage keyRequest = DHKeyExchangeRequestMessage.from(request.getPayload());
		DHKeyExchange dh = new DHKeyExchange();
		BigInteger serverPublicKey = dh.getPublicKey();
		DHKeyExchangeResponseMessage keyResponse = new DHKeyExchangeResponseMessage(serverPublicKey, id);

		DatagramMessage response = new DatagramMessage(request.getCounterpartAddress(), keyResponse.toByteArray());
		datagramEndpoint.send(response);

		BigInteger sharedKey = dh.getSharedKey(keyRequest.getClientPublicKey());
		Key key = Key.fromBytes(sharedKey.toByteArray());

		return Optional.of(new IdKeyPair(keyResponse.getId(), key));
	}

}
