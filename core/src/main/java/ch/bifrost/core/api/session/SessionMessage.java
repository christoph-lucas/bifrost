package ch.bifrost.core.api.session;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a message on the session layer with String content.
 */
@Data
@AllArgsConstructor
public class SessionMessage {

	private final byte[] payload;
	
}
