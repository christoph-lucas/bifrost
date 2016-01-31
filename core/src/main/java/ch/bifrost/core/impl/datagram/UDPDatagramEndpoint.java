package ch.bifrost.core.impl.datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ch.bifrost.core.api.datagram.DatagramEndpoint;

/**
 * Implementation of {@link DatagramEndpoint} for UDP.
 */
public class UDPDatagramEndpoint implements DatagramEndpoint {

	private static final int SOCKET_TIMEOUT_IN_MS = 1000;
	private DatagramSocket socket;
	private BlockingQueue<Packet> receivedPackets = new LinkedBlockingQueue<>();
	private Receiver receiver;

	/**
	 * Create an endpoint that listens on a specific port (usually required for the server). 
	 * @param port the port to listen on
	 * @throws SocketException when the endpoint cannot be created
	 */
	public UDPDatagramEndpoint(int port) throws SocketException {
		socket = new DatagramSocket(port);
		initialize();
	}

	/**
	 * Create an endpoint on any arbitrary port.
	 * @throws SocketException thrown if endpoint cannot be created
	 */
	public UDPDatagramEndpoint() throws SocketException {
		socket = new DatagramSocket();
		initialize();
	}
	
	private void initialize() throws SocketException {
		socket.setSoTimeout(SOCKET_TIMEOUT_IN_MS);
		receiver = new Receiver(socket, receivedPackets);
		receiver.start();
	}
	
	@Override
	public void close() throws IOException {
		receiver.cancel();
		try {
			Thread.sleep(2*SOCKET_TIMEOUT_IN_MS);
		} catch (InterruptedException e) {
			socket.close();
		}
		socket.close();
	}
	
	@Override
	public void send(Packet outgoing) throws IOException {
		socket.send(outgoing.toDatagram());
	}
	
	@Override
	public Packet receive() throws InterruptedException {
		return receivedPackets.take();
	}
	
	private static class Receiver extends Thread {
		
		private DatagramSocket localSocket;
		private BlockingQueue<Packet> queue;
		private boolean cancelled;

		public Receiver(DatagramSocket socket, BlockingQueue<Packet> queue) {
			localSocket = socket;
			this.queue = queue;
		}
		
		@Override
		public void run() {
			while(!cancelled) {
				byte[] incomingBuffer = new byte[1024];
				DatagramPacket datagram = new DatagramPacket(incomingBuffer, incomingBuffer.length);
				try {
					localSocket.receive(datagram);
				} catch (IOException e) {
					continue;
				}
				Packet packet = Packet.fromDatagram(datagram);
				try {
					queue.put(packet);
				} catch (InterruptedException e) {
					System.out.println("Got interrupted and therefore lost a packet.");
				}
			}
		}
		
		public void cancel() {
			cancelled = true;
		}
		
	}
	

}
