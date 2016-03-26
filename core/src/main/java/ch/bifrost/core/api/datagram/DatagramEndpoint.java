package ch.bifrost.core.api.datagram;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

/**
 * An endpoint on the datagram layer.
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
	 * @throws IOException thrown when an error occurred during the reception of a packet 
	 */
	Packet receive() throws IOException;
	
	/**
	 * Blocking call to receive the next message, waiting at most for the timeout.
	 * @return the next message or null if timeout exceeded
	 * @throws IOException thrown when an error occurred during the reception of a packet 
	 */
	Optional<Packet> receive(long timeout, TimeUnit unit) throws IOException;
	
}