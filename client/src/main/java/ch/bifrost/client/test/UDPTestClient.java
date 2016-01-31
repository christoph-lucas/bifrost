package ch.bifrost.client.test;

import ch.bifrost.core.impl.datagram.Packet;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;

public class UDPTestClient {

	public static void main(String[] args) throws Exception {
		final String serverHost = "localhost";
		final int serverPort = 34543;
		UDPDatagramEndpoint clientEndpoint = new UDPDatagramEndpoint();
		
		System.out.println("Client sending a message.");
		String msg = "Hello World!";
		Packet outgoing = new Packet(serverHost, serverPort, msg);
		clientEndpoint.send(outgoing);
		System.out.println("Client sent a message: " + msg);
		
		Packet incoming = clientEndpoint.receive();
		String receivedMessage = incoming.getContent();
		System.out.println("Client received message: " + receivedMessage);
		
		clientEndpoint.close();
	}
	
}
