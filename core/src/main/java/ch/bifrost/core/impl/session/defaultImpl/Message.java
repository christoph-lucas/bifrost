package ch.bifrost.core.impl.session.defaultImpl;

import org.apache.commons.lang3.ArrayUtils;

import ch.bifrost.core.api.session.SessionMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the messages within the default session layer.
 * 
 * FORMAT: DefaultSessionLayerMessageIdentifier (1 byte) | payload
 */
@Data
@AllArgsConstructor
public class Message {

	private final MessageIdentifier identifier;
	private final byte[] payload;

	public static Message from (SessionMessage message) {
		byte[] byteRepresentation = message.getPayload();

		byte[] identifierBytes = ArrayUtils.subarray(byteRepresentation, 0, MessageIdentifier.BYTE_REPRESENTATION_LENGTH);
		MessageIdentifier identifier = MessageIdentifier.from(identifierBytes);
		byte[] payload = ArrayUtils.subarray(byteRepresentation, MessageIdentifier.BYTE_REPRESENTATION_LENGTH, byteRepresentation.length);

		return new Message(identifier, payload);
	}

	public SessionMessage toSessionMessage () {
		byte[] identifierBytes = identifier.getByteVal();
		byte[] sessionMessagePayload = ArrayUtils.addAll(identifierBytes, payload);
		return new SessionMessage(sessionMessagePayload);
	}

}
