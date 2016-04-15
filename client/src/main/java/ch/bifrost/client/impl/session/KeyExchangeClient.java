package ch.bifrost.client.impl.session;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.Packet;
import ch.bifrost.core.api.session.IdKeyPair;

public class KeyExchangeClient {

	private DatagramEndpoint datagramEndpoint;
	private InetAddress serverHost;
	private int serverPort;

	public KeyExchangeClient(DatagramEndpoint datagramEndpoint, InetAddress serverHost, int serverPort) {
		this.datagramEndpoint = datagramEndpoint;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}
	
	public Optional<IdKeyPair> get(long timeout, TimeUnit unit) throws Exception {
		Packet request = new Packet(serverHost, serverPort, "Key Exchange Request");
		datagramEndpoint.send(request);
		Optional<Packet> response = datagramEndpoint.receive(timeout, unit);
		if (response.isPresent()) {
			return Optional.of(IdKeyPair.decode(response.get().getContent()));
		}
		return Optional.absent();
	}

}
