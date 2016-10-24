package ch.bifrost.core.api.keyexchange;

import org.apache.commons.lang3.ArrayUtils;

import ch.bifrost.core.api.session.MultiplexingID;
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

	private final MultiplexingID id;
	private final byte[] key;

	public static IdKeyPair decode (byte[] byteRepresentation) {
		byte[] multiplexingIdBytes = ArrayUtils.subarray(byteRepresentation, 0, MultiplexingID.LENGTH_IN_BYTES);
		MultiplexingID multiplexingId = MultiplexingID.fromBytes(multiplexingIdBytes);
		byte[] key = ArrayUtils.subarray(byteRepresentation, MultiplexingID.LENGTH_IN_BYTES, byteRepresentation.length);

		return new IdKeyPair(multiplexingId, key);
	}

	public byte[] encode () {
		byte[] sessionIdBytesCorrectLength = id.toBytes();
		return ArrayUtils.addAll(sessionIdBytesCorrectLength, key);
	}

}
