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
	 * 
	 * @param packet the packet to be sent
	 * @throws IOException thrown if an error occurs
	 */
	void send (DatagramMessage packet) throws IOException;

	/**
	 * Blocking call to receive the next message.
	 * 
	 * @return the next message
	 * @throws IOException thrown when an error occurred during the reception of a packet
	 */
	DatagramMessage receive () throws IOException, InterruptedException;

	/**
	 * Blocking call to receive the next message, waiting at most for the timeout.
	 * 
	 * @return the next message or null if timeout exceeded
	 * @throws IOException thrown when an error occurred during the reception of a packet
	 */
	Optional<DatagramMessage> receive (long timeout, TimeUnit unit) throws IOException, InterruptedException;

	/**
	 * A datagram endpoint can operate in two different modes: either the counterpart address is fixed, or it is
	 * provided with each message. After setting the address with this method, the address in the message is
	 * ignored.
	 * 
	 * @param counterpartAddress The fixed counterpart address to be used for all messages.
	 * @return the instance of this endpoint for fluent access
	 */
	DatagramEndpoint counterpartAddress (CounterpartAddress counterpartAddress);

}