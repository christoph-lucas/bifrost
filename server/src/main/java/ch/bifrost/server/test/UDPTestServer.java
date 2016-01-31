package ch.bifrost.server.test;

import ch.bifrost.core.api.datagram.Packet;
import ch.bifrost.core.api.datagram.UDPDatagramEndpoint;

public class UDPTestServer {

	public static void main (String[] args) throws Exception {
		UDPDatagramEndpoint serverEndpoint = new UDPDatagramEndpoint(34543);
		
		Packet incoming = serverEndpoint.receive();
		String receivedMessage = incoming.getContent();
		System.out.println("Server received message: " + receivedMessage);
		
		System.out.println("Server sending return.");
		String returnMessage = "Simon says: " + receivedMessage;
		Packet outgoing = new Packet(incoming.getCounterpartAddress(), incoming.getCounterpartPort(), returnMessage);
		serverEndpoint.send(outgoing);
		System.out.println("Server sent a reply: " + returnMessage);
		
		serverEndpoint.close();
	}
	
}
