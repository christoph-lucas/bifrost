package ch.bifrost.client.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DatagramClientWithReply {

	private String host;
	private int port;
	
	public String send(String message) throws IOException {
		byte[] messageBytes = message.getBytes();
		InetAddress address = InetAddress.getByName(host);

		DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);

		byte[] incomingBuffer = new byte[1024];
		packet = new DatagramPacket(incomingBuffer, incomingBuffer.length);
		socket.receive(packet);
		socket.close();

		String received = new String(packet.getData(), 0, packet.getLength());
		return received;
	}
}
