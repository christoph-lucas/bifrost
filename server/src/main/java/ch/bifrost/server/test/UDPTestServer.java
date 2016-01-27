package ch.bifrost.server.test;

import ch.bifrost.server.impl.datagram.SimonSaysDatagramPacketHandlerFactory;
import ch.bifrost.server.impl.datagram.UDPDatagramServer;

public class UDPTestServer {

	public static void main (String[] args) throws Exception {
		UDPDatagramServer server = new UDPDatagramServer(34543, new SimonSaysDatagramPacketHandlerFactory());
		server.run();
	}
	
}
