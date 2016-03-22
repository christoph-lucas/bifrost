package ch.bifrost.core.api.session;

import java.io.IOException;

import ch.bifrost.core.api.datagram.DatagramLayerAdapter;

/**
 * Actually just an abstraction of a {@link DatagramLayerAdapter} for {@link SessionPacket}s.
 */
public interface SessionPacketSender {

	/**
	 * @param sessionPacket packet to be sent to the counterpart
	 * @throws IOException thrown if something goes wrong
	 */
	void send(SessionPacket sessionPacket) throws IOException;

}
