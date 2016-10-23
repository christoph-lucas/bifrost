package ch.bifrost.core.api.keyexchange;

import org.apache.commons.lang3.ArrayUtils;

import ch.bifrost.core.impl.MessageCodecUtils;
import ch.bifrost.core.impl.datagram.DatagramMessageWithId;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A pair of Multiplexing ID and Key.
 * 
 * FORMAT: Multiplexing ID (32 bytes) | Key
 */
@Data
@AllArgsConstructor
public class IdKeyPair {

	private final String id;
	private final byte[] key;

	public static IdKeyPair decode (byte[] byteRepresentation) {
		byte[] sessionIdBytes = ArrayUtils.subarray(byteRepresentation, 0, DatagramMessageWithId.MULTIPLEXING_ID_LENGTH_IN_BYTES);
		String sessionId = MessageCodecUtils.decodeStringFromByteArray(sessionIdBytes);
		byte[] key = ArrayUtils.subarray(byteRepresentation, DatagramMessageWithId.MULTIPLEXING_ID_LENGTH_IN_BYTES, byteRepresentation.length);

		return new IdKeyPair(sessionId, key);
	}

	public byte[] encode () {
		byte[] sessionIdBytesCorrectLength = MessageCodecUtils.encodeStringAsByteArrayWithFixedLength(id, DatagramMessageWithId.MULTIPLEXING_ID_LENGTH_IN_BYTES);
		return ArrayUtils.addAll(sessionIdBytesCorrectLength, key);
	}

}
