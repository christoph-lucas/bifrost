package ch.bifrost.core.api.session;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * Represents a message on the session layer with String content.
 */
@Data
public class SessionMessage {

	public static final String COUNTERPART_ADDRESS = "COUNTERPART_ADDRESS";
	
	private final byte[] payload;
	private final Map<String, Object> contextData = new HashMap<>();
	
	public SessionMessage(byte[] payload) {
		this.payload = payload;
	}
	
	public static SessionMessage from(SessionInternalMessage sessionInternalMessage) {
		SessionMessage sessionMessage = new SessionMessage(sessionInternalMessage.getPayload());
		sessionMessage.getContextData().put(COUNTERPART_ADDRESS, sessionInternalMessage.getCounterpartAddress());
		return sessionMessage;
	}
	
}
