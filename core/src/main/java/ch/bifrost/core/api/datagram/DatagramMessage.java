package ch.bifrost.core.api.datagram;

import java.net.DatagramPacket;

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
	private CounterpartAddress counterpartAddress;
	@Getter
	private byte[] payload;

	public static DatagramMessage from (DatagramPacket datagram) {
		byte[] udpPacketPayload = Arrays.copyOfRange(datagram.getData(), 0, datagram.getLength());
		CounterpartAddress counterpartAddress = new CounterpartAddress(datagram.getAddress(), datagram.getPort());
		return new DatagramMessage(counterpartAddress, udpPacketPayload);
	}

	public DatagramPacket toUdpPacket () {
		return new DatagramPacket(payload, payload.length, counterpartAddress.getIp(), counterpartAddress.getPort());
	}

}
