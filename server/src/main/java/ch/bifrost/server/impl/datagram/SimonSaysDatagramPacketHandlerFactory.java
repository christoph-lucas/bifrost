package ch.bifrost.server.impl.datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import ch.bifrost.core.api.datagram.DatagramPacketHandler;
import ch.bifrost.core.api.datagram.DatagramPacketHandlerFactory;
import ch.bifrost.core.api.datagram.IncomingPacket;

public class SimonSaysDatagramPacketHandlerFactory implements DatagramPacketHandlerFactory {

	
	@Override
	public SimonSaysHandler getHandlerFor(IncomingPacket packet, DatagramSocket socket) {
		return new SimonSaysHandler(packet, socket);
	}
	
	public static class SimonSaysHandler implements DatagramPacketHandler {

		private IncomingPacket packet;
		private DatagramSocket socket;

		public SimonSaysHandler(IncomingPacket packet, DatagramSocket socket) {
			this.packet = packet;
			this.socket = socket;
		}
		
		@Override
		public void run() {
			String received = new String(packet.getReceivedBytes(), 0, packet.getReceivedLength());
			System.out.println("Server: Received message '" + received + "'.");
			
			System.out.println("Server: Sending return.");
			String returnMessage = "Simon says: " + received;

			byte[] outgoingBuffer = returnMessage.getBytes();
			DatagramPacket datagram = new DatagramPacket(outgoingBuffer, outgoingBuffer.length, packet.getAddress(), packet.getPort());
			
			try {
				socket.send(datagram);
			} catch (IOException e) {
				System.out.println("Server: Could not reply to message...");
			}
			System.out.println("Server: Sent a reply...");
		}
		
	}
	
}
