package ch.bifrost.core.api.datagram;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

/**
 * An adapter on the datagram layer.
 */
public interface DatagramLayerAdapter extends Closeable {

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
	
	/**
	 * Blocking call to receive the next message, waiting at most for the timeout.
	 * @return the next message or null if timeout exceeded
	 * @throws InterruptedException thrown when interrupted during wait for next message 
	 */
	Optional<Packet> receive(long timeout, TimeUnit unit) throws InterruptedException;
	
}