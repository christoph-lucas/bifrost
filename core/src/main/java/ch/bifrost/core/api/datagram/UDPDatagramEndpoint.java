package ch.bifrost.core.api.datagram;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPDatagramEndpoint implements Closeable {

	private static final int SOCKET_TIMEOUT_IN_MS = 1000;
	private DatagramSocket socket;
	private BlockingQueue<Packet> receivedPackets = new LinkedBlockingQueue<>();
	private Receiver receiver;

	public UDPDatagramEndpoint(int port) throws SocketException {
		socket = new DatagramSocket(port);
		initialize();
	}

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
	
	public void send(Packet outgoing) throws IOException {
		DatagramPacket datagram = new DatagramPacket(outgoing.getBytes(), outgoing.getLength(), outgoing.getCounterpartAddress(), outgoing.getCounterpartPort());
		socket.send(datagram);
	}
	
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
				Packet packet = new Packet(datagram.getAddress(), datagram.getPort(), datagram.getData(), datagram.getLength());
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
