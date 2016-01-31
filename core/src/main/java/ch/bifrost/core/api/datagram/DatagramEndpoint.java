package ch.bifrost.core.api.datagram;

import java.io.Closeable;
import java.io.IOException;

import ch.bifrost.core.impl.datagram.Packet;

/**
 * Represents an endpoint on the datagram layer.
 */
public interface DatagramEndpoint extends Closeable {

	/**
	 * Send the given packet to the address given in the packet.
	 * @param packet the packet to be sent
	 * @throws IOException thrown if an error occurrs
	 */
	void send(Packet packet) throws IOException;
	
	/**
	 * Blocking call to receive the next message.
	 * @return the next message
	 * @throws InterruptedException thrown when interrupted during wait for next message 
	 */
	Packet receive() throws InterruptedException;
	
}