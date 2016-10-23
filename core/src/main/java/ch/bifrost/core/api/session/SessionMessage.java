package ch.bifrost.core.api.session;

import java.util.HashMap;
import java.util.Map;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramMessage;
import lombok.Data;

/**
 * Represents a message on the session layer with byte content.
 */
@Data
public class SessionMessage {

	public static final String COUNTERPART_ADDRESS = "COUNTERPART_ADDRESS";

	private final byte[] payload;
	private final Map<String, Object> contextData = new HashMap<>();

	public SessionMessage (byte[] payload) {
		this.payload = payload;
	}

	public static SessionMessage from (DatagramMessage datagram) {
		SessionMessage sessionMessage = new SessionMessage(datagram.getPayload());
		sessionMessage.getContextData().put(COUNTERPART_ADDRESS, datagram.getCounterpartAddress());
		return sessionMessage;
	}

	public DatagramMessage toDatagramMessage () {
		return new DatagramMessage((CounterpartAddress) contextData.get(COUNTERPART_ADDRESS), payload);
	}

}
