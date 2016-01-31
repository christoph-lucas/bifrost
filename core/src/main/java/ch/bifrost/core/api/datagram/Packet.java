package ch.bifrost.core.api.datagram;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Packet {
	private InetAddress counterpartAddress;
	private int counterpartPort;
	private byte[] bytes;
	private int length;
	
	public Packet(InetAddress counterpartAddress, int counterpartPort, String content) {
		this.counterpartAddress = counterpartAddress;
		setFields(counterpartPort, content);
	}

	public Packet(String host, int counterpartPort, String content) throws UnknownHostException {
		this.counterpartAddress = InetAddress.getByName(host);
		setFields(counterpartPort, content);
	}
	
	private void setFields(int counterpartPort, String content) {
		this.counterpartPort = counterpartPort;
		bytes = content.getBytes();
		length = bytes.length;
	}
	
	public String getContent() {
		// TODO Encoding and decoding with Base64
		return new String(bytes, 0, length);
	}
	
	
}
