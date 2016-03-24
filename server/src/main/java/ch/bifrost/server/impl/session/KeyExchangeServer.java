package ch.bifrost.server.impl.session;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.Packet;
import ch.bifrost.core.api.session.IdKeyPair;
import ch.bifrost.core.api.session.KeyExchange;

public class KeyExchangeServer implements KeyExchange {

	private DatagramEndpoint datagramEndpoint;

	public KeyExchangeServer(DatagramEndpoint datagramEndpoint) {
		this.datagramEndpoint = datagramEndpoint;
	}
	
	@Override
	public Optional<IdKeyPair> get(long timeout, TimeUnit unit) throws Exception {
		Optional<Packet> receiveOpt = datagramEndpoint.receive(timeout, unit);
		if (!receiveOpt.isPresent()) {
			return Optional.absent();
		}
		Packet request = receiveOpt.get();
		
		String id = ThreadLocalRandom.current().ints(0, 9).limit(30).mapToObj(Integer::toString).collect(Collectors.joining());
		byte[] key = new byte[0];
		IdKeyPair idKeyPair = new IdKeyPair(id, key);
		
		Packet response = new Packet(request.getCounterpartAddress(), request.getCounterpartPort(), idKeyPair.encode());
		datagramEndpoint.send(response);
		
		return Optional.of(idKeyPair);
	}

}
