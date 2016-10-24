package ch.bifrost.core.impl.datagram;

import java.io.IOException;

import ch.bifrost.core.api.datagram.DatagramEndpoint;

/**
 * Actually just an abstraction of a {@link DatagramEndpoint} for {@link DatagramMessageWithId}s.
 */
public class DatagramMessageWithIdSender {

	private DatagramEndpoint endpoint;

	public DatagramMessageWithIdSender (DatagramEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * @param messageWithId packet to be sent to the counterpart
	 * @throws IOException thrown if something goes wrong
	 */
	public void send (DatagramMessageWithId sessionPacket) throws IOException {
		endpoint.send(sessionPacket.toDatagramMessage());
	}

}
