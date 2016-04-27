package ch.bifrost.client.impl.session;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.session.IdKeyPair;
import ch.bifrost.core.impl.MessageCodecUtils;

public class KeyExchangeClient {

	private DatagramEndpoint datagramEndpoint;
	private CounterpartAddress serverAddress;

	public KeyExchangeClient(DatagramEndpoint datagramEndpoint, CounterpartAddress serverAddress) {
		this.datagramEndpoint = datagramEndpoint;
		this.serverAddress = serverAddress;
	}
	
	public Optional<IdKeyPair> get(long timeout, TimeUnit unit) throws Exception {
		byte[] messageBytes = MessageCodecUtils.encodeStringAsByteArray("Key Exchange Request");
		DatagramMessage request = new DatagramMessage(serverAddress, messageBytes);
		datagramEndpoint.send(request);
		Optional<DatagramMessage> response = datagramEndpoint.receive(timeout, unit);
		if (response.isPresent()) {
			return Optional.of(IdKeyPair.decode(response.get().getPayload()));
		}
		return Optional.absent();
	}

}
