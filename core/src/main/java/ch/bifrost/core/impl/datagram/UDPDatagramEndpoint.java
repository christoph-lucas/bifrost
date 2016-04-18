package ch.bifrost.core.impl.datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * Implementation of {@link DatagramEndpoint} for UDP.
 */
public class UDPDatagramEndpoint implements DatagramEndpoint {

	@Getter
	@Setter
	private int receiverBufferSizeInBytes = 1024;
	
	private DatagramSocket socket;

	/**
	 * Create an endpoint that listens on a specific port (usually required for the server). 
	 * @param port the port to listen on
	 * @throws SocketException when the endpoint cannot be created
	 */
	public UDPDatagramEndpoint(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	/**
	 * Create an endpoint on any arbitrary port.
	 * @throws SocketException thrown if endpoint cannot be created
	 */
	public UDPDatagramEndpoint() throws SocketException {
		socket = new DatagramSocket();
	}
	
	@Override
	public void close() throws IOException {
		socket.close();
	}
	
	@Override
	public void send(DatagramMessage outgoing) throws IOException {
		socket.send(outgoing.toUdpPacket());
	}
	
	@Override
	public DatagramMessage receive() throws IOException {
		byte[] incomingBuffer = new byte[receiverBufferSizeInBytes];
		DatagramPacket datagram = new DatagramPacket(incomingBuffer, incomingBuffer.length);
		synchronized(socket) {
			socket.setSoTimeout(0);
			socket.receive(datagram);
		}
		return DatagramMessage.from(datagram);
	}
	
	@Override
	public Optional<DatagramMessage> receive(long timeout, TimeUnit unit) throws IOException {
		long socketTimeoutInMs = unit.toMillis(timeout);
		byte[] incomingBuffer = new byte[receiverBufferSizeInBytes];
		DatagramPacket datagram = new DatagramPacket(incomingBuffer, incomingBuffer.length);
		synchronized(socket) {
			socket.setSoTimeout(Long.valueOf(socketTimeoutInMs).intValue());
			try {
				socket.receive(datagram);
			} catch (SocketTimeoutException e) {
				return Optional.absent();
			}
		}
		return Optional.of(DatagramMessage.from(datagram));
	}

}
