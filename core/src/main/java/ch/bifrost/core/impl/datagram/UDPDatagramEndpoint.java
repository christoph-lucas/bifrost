package ch.bifrost.core.impl.datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.Packet;
import lombok.Getter;
import lombok.Setter;

/**
 * Implementation of {@link DatagramEndpoint} for UDP.
 */
public class UDPDatagramEndpoint implements DatagramEndpoint {

	@Getter
	@Setter
	private int socketTimeoutInMs = 1000;
	
	@Getter
	@Setter
	private int receiverBufferSizeInBytes = 1024;
	
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
		socket.setSoTimeout(socketTimeoutInMs);
		receiver = new Receiver(socket, receivedPackets);
		receiver.start();
	}
	
	@Override
	public void close() throws IOException {
		receiver.cancel();
		try {
			Thread.sleep(2*socketTimeoutInMs);
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
	
	private class Receiver extends Thread {
		
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
				byte[] incomingBuffer = new byte[receiverBufferSizeInBytes];
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
