package ch.bifrost.core.api.datagram;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.bouncycastle.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents datagram packets with String-Content that can be sent over an {@link DatagramEndpoint}.
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class DatagramMessage {
	@Getter
	private InetAddress counterpartAddress;
	@Getter
	private int counterpartPort;
	@Getter
	private byte[] payload;
	
	public DatagramMessage(String host, int counterpartPort, byte[] payload) throws UnknownHostException {
		this.counterpartAddress = InetAddress.getByName(host);
		this.counterpartPort = counterpartPort;
		this.payload = payload;
	}
	
	public static DatagramMessage from(DatagramPacket datagram) {
		byte[] udpPacketPayload = Arrays.copyOfRange(datagram.getData(), 0, datagram.getLength());
		return new DatagramMessage(datagram.getAddress(), datagram.getPort(), udpPacketPayload);
	}
	
	public DatagramPacket toUdpPacket() {
		return new DatagramPacket(payload, payload.length, counterpartAddress, counterpartPort);
	}
	
}
