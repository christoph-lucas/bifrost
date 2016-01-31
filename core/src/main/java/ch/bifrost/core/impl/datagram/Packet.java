package ch.bifrost.core.impl.datagram;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents packets with String-Content that can be sent over an {@link DatagramEndpoint}.
 */
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Packet {
	@Getter
	private InetAddress counterpartAddress;
	@Getter
	private int counterpartPort;
	private byte[] bytes;
	private int length;
	
	public static Packet fromDatagram(DatagramPacket datagram) {
		return new Packet(datagram.getAddress(), datagram.getPort(), datagram.getData(), datagram.getLength());
	}
	
	public Packet(InetAddress counterpartAddress, int counterpartPort, String content) {
		this.counterpartAddress = counterpartAddress;
		setFields(counterpartPort, content);
	}

	public Packet(String host, int counterpartPort, String content) throws UnknownHostException {
		this.counterpartAddress = InetAddress.getByName(host);
		setFields(counterpartPort, content);
	}
	
	public DatagramPacket toDatagram() {
		return new DatagramPacket(bytes, length, counterpartAddress, counterpartPort);
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
