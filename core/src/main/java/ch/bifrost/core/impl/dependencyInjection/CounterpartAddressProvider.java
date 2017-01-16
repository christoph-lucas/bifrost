package ch.bifrost.core.impl.dependencyInjection;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.inject.Provider;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CounterpartAddressProvider implements Provider<CounterpartAddress> {

	private final InetAddress ip;
	private final int port;

	public CounterpartAddressProvider(String host, int port) {
		this.port = port;
		try {
			ip = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			throw new DependencyInjectionException("Cannot resolve Host: " + e.getMessage(), e);
		}
	}
	
	@Override
	public CounterpartAddress get () {
		return new CounterpartAddress(ip, port);
	}

}
