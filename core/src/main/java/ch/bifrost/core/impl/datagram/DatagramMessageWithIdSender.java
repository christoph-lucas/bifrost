package ch.bifrost.core.impl.datagram;

import java.io.IOException;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.session.MultiplexingID;

/**
 * Actually just an abstraction of a {@link DatagramEndpoint} for {@link DatagramMessageWithId}s.
 */
public class DatagramMessageWithIdSender {

	private DatagramEndpoint endpoint;
	private MultiplexingID id;

	public DatagramMessageWithIdSender (DatagramEndpoint endpoint, MultiplexingID id) {
		this.endpoint = endpoint;
		this.id = id;
	}

	/**
	 * Attaches the ID to the datagram and sends it.
	 * 
	 * @param datagram packet to be sent to the counterpart
	 * @throws IOException thrown if something goes wrong
	 */
	public void send (DatagramMessage datagram) throws IOException {
		DatagramMessageWithId msgWithId = new DatagramMessageWithId(datagram.getCounterpartAddress(), datagram.getPayload(), this.id);
		endpoint.send(msgWithId.toDatagramMessage());
	}

}
