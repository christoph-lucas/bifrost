package ch.bifrost.core.api.datagram;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.bouncycastle.util.Arrays;

import com.google.common.base.Charsets;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents datagram packets with String-Content that can be sent over an {@link DatagramLayerAdapter}.
 */
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class Packet {
	@Getter
	private InetAddress counterpartAddress;
	@Getter
	private int counterpartPort;
	private byte[] bytes;
	
	public static Packet fromDatagram(DatagramPacket datagram) {
		byte[] content = Arrays.copyOfRange(datagram.getData(), 0, datagram.getLength());
		return new Packet(datagram.getAddress(), datagram.getPort(), content);
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
		return new DatagramPacket(bytes, bytes.length, counterpartAddress, counterpartPort);
	}
	
	private void setFields(int counterpartPort, String content) {
		this.counterpartPort = counterpartPort;
		bytes = encode(content);
	}
	
	public String getContent() {
		return decode(bytes);
	}
	
	private byte[] encode(String input) {
		return input.getBytes(Charsets.UTF_8);
	}
	
	private String decode(byte[] bytes) {
		return new String(bytes, Charsets.UTF_8);
	}
	
	
}
