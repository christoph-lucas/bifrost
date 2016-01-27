package ch.bifrost.server.impl.datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import ch.bifrost.core.api.datagram.DatagramPacketHandlerFactory;
import ch.bifrost.core.api.datagram.IncomingPacket;

public class UDPDatagramServer implements Runnable  {

	private DatagramSocket socket;
	private DatagramPacketHandlerFactory handlerFactory;

	public UDPDatagramServer(int port, DatagramPacketHandlerFactory handlerFactory) throws SocketException {
		this.handlerFactory = handlerFactory;
		socket = new DatagramSocket(port);
	}
	
	@Override
	public void run() {
		while(true) {
			byte[] incomingBuffer = new byte[1024];
			DatagramPacket datagram = new DatagramPacket(incomingBuffer, incomingBuffer.length);
			System.out.println("Server: Waiting for message...");
			try {
				socket.receive(datagram);
				IncomingPacket packet = new IncomingPacket(datagram.getAddress(), datagram.getPort(), datagram.getData(), datagram.getLength());
				new Thread(handlerFactory.getHandlerFor(packet, socket)).run(); 
			} catch (IOException e) {
				System.out.println("Server: Something went wrong receiving a message. Going for next turn!");
			}
		}
		
//		socket.close();

	}
	
}
