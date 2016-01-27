package ch.bifrost.core.api.datagram;

import java.net.InetAddress;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IncomingPacket {
	private InetAddress address;
	private int port;
	private byte[] receivedBytes;
	private int receivedLength;
}
