package ch.bifrost.core.api.keyexchange;

import org.apache.commons.lang3.ArrayUtils;

import ch.bifrost.core.api.datagram.MultiplexingID;
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
	private final Key key;

	public static IdKeyPair decode (byte[] byteRepresentation) {
		byte[] multiplexingIdBytes = ArrayUtils.subarray(byteRepresentation, 0, MultiplexingID.LENGTH_IN_BYTES);
		byte[] keyBytes = ArrayUtils.subarray(byteRepresentation, MultiplexingID.LENGTH_IN_BYTES, byteRepresentation.length);
		return new IdKeyPair(MultiplexingID.fromBytes(multiplexingIdBytes), Key.fromBytes(keyBytes));
	}

	public byte[] encode () {
		return ArrayUtils.addAll(id.toBytes(), key.toBytes());
	}

}
