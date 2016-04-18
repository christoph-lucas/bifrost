package ch.bifrost.server.impl.session;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.Packet;
import ch.bifrost.core.api.session.IdKeyPair;

public class KeyExchangeServer  {

	private DatagramEndpoint datagramEndpoint;

	public KeyExchangeServer(DatagramEndpoint datagramEndpoint) {
		this.datagramEndpoint = datagramEndpoint;
	}
	
	public Optional<IdKeyPair> get(String id, long timeout, TimeUnit unit) throws Exception {
		Optional<Packet> receiveOpt = datagramEndpoint.receive(timeout, unit);
		if (!receiveOpt.isPresent()) {
			return Optional.absent();
		}
		Packet request = receiveOpt.get();
		
		byte[] key = new byte[0];
		IdKeyPair idKeyPair = new IdKeyPair(id, key);
		
		Packet response = new Packet(request.getCounterpartAddress(), request.getCounterpartPort(), idKeyPair.encode());
		datagramEndpoint.send(response);
		
		return Optional.of(idKeyPair);
	}

}
