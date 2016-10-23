package ch.bifrost.core.api.datagram;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CounterpartAddress {

	private final InetAddress ip;
	private final int port;

	public CounterpartAddress (String host, int port) throws UnknownHostException {
		ip = InetAddress.getByName(host);
		this.port = port;
	}

	public boolean isValid () {
		return ip != null && port != 0;
	}

}
