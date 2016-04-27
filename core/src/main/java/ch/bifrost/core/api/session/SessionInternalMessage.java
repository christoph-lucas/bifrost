package ch.bifrost.core.api.session;

import org.apache.commons.lang3.ArrayUtils;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.impl.MessageCodecUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A message inside the session layer, not used outside the session layer.
 * 
 * FORMAT: Session ID (32 bytes) | payload
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class SessionInternalMessage {

	
	private CounterpartAddress counterpartAddress;
	private String sessionId;
	private byte[] payload;

	public static SessionInternalMessage from(DatagramMessage packet) {
		byte[] byteRepresentation = packet.getPayload();
		
		byte[] sessionIdBytes = ArrayUtils.subarray(byteRepresentation, 0, SessionConverter.SESSION_ID_LENGTH_IN_BYTES);
		String sessionId = MessageCodecUtils.decodeStringFromByteArray(sessionIdBytes);
		byte[] payload = ArrayUtils.subarray(byteRepresentation, SessionConverter.SESSION_ID_LENGTH_IN_BYTES, byteRepresentation.length);
		
		return new SessionInternalMessage(packet.getCounterpartAddress(), sessionId, payload);
	}

	
	public DatagramMessage toDatagramMessage() {
		byte[] sessionIdBytesCorrectLength = MessageCodecUtils.encodeStringAsByteArrayWithFixedLength(sessionId, SessionConverter.SESSION_ID_LENGTH_IN_BYTES);
		byte[] datagramMessagePayload = ArrayUtils.addAll(sessionIdBytesCorrectLength, payload);
		return new DatagramMessage(counterpartAddress, datagramMessagePayload);
	}

	
}