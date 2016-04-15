package ch.bifrost.core.api.session;

import org.apache.commons.lang3.ArrayUtils;

import ch.bifrost.core.impl.MessageCodecUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A pair of Session ID and Key.
 * 
 * FORMAT: Session ID (32 bytes) | Key
 */
@Data
@AllArgsConstructor
public class IdKeyPair {

	private final String id;
	private final byte[] key;
	
	public static IdKeyPair decode(byte[] byteRepresentation) {
		byte[] sessionIdBytes = ArrayUtils.subarray(byteRepresentation, 0, SessionConverter.SESSION_ID_LENGTH_IN_BYTES);
		String sessionId = MessageCodecUtils.decodeStringFromByteArray(sessionIdBytes);
		byte[] key = ArrayUtils.subarray(byteRepresentation, SessionConverter.SESSION_ID_LENGTH_IN_BYTES, byteRepresentation.length);
		
		return new IdKeyPair(sessionId, key);
	}
	
	public byte[] encode() {
		byte[] sessionIdBytesCorrectLength = MessageCodecUtils.encodeStringAsByteArrayWithFixedLength(id, SessionConverter.SESSION_ID_LENGTH_IN_BYTES);
		return ArrayUtils.addAll(sessionIdBytesCorrectLength, key);
	}
	
}
