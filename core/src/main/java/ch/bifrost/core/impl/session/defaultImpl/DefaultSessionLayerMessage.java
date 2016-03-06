package ch.bifrost.core.impl.session.defaultImpl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionException;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the messages within the default session layer.
 */
@Data
@AllArgsConstructor
public class DefaultSessionLayerMessage {

	private final DefaultSessionLayerMessageIdentifier identifier;
	private final String payload;

	
	public static DefaultSessionLayerMessage fromMessage(Message message) {
		String rawContent = message.getContent();
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(rawContent, DefaultSessionLayerMessage.class);
		} catch (IOException e) {
			throw new SessionException("Cannot decode DefaultSessionLayerMessage from Json.", e);
		}
	}

}
