package ch.bifrost.core.impl.dependencyInjection;

import java.net.SocketException;

import com.google.inject.Provider;

import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UdpDatagramEndpointProvider implements Provider<UDPDatagramEndpoint> {

	private int port;

	@Override
	public UDPDatagramEndpoint get () {
		try {
			return new UDPDatagramEndpoint(port);
		} catch (SocketException e) {
			throw new RuntimeException();
		}
	}

}
