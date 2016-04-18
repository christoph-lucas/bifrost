package ch.bifrost.core.api.session;

import java.io.IOException;

import ch.bifrost.core.api.datagram.DatagramEndpoint;

/**
 * Actually just an abstraction of a {@link DatagramEndpoint} for {@link SessionInternalMessage}s.
 */
public interface SessionInternalMessageSender {

	/**
	 * @param sessionPacket packet to be sent to the counterpart
	 * @throws IOException thrown if something goes wrong
	 */
	void send(SessionInternalMessage sessionPacket) throws IOException;

}
