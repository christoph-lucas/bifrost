package ch.bifrost.core.impl.datagram;

import org.apache.commons.lang3.ArrayUtils;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.session.MultiplexingID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A datagram message with a multiplexing ID.
 * 
 * FORMAT: Session ID (32 bytes) | payload
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatagramMessageWithId extends DatagramMessage {

	private MultiplexingID multiplexingId;

	public DatagramMessageWithId (CounterpartAddress address, byte[] payload, MultiplexingID multiplexingId) {
		super(address, payload);
		this.multiplexingId = multiplexingId;
	}

	public static DatagramMessageWithId from (DatagramMessage packet) {
		byte[] byteRepresentation = packet.getPayload();

		byte[] multiplexingIdBytes = ArrayUtils.subarray(byteRepresentation, 0, MultiplexingID.LENGTH_IN_BYTES);
		MultiplexingID multiplexingId = MultiplexingID.fromBytes(multiplexingIdBytes);
		byte[] payload = ArrayUtils.subarray(byteRepresentation, MultiplexingID.LENGTH_IN_BYTES, byteRepresentation.length);

		return new DatagramMessageWithId(packet.getCounterpartAddress(), payload, multiplexingId);
	}

	public DatagramMessage toDatagramMessage () {
		byte[] multiplexingIdBytesCorrectLength = multiplexingId.toBytes();
		byte[] datagramMessagePayload = ArrayUtils.addAll(multiplexingIdBytesCorrectLength, getPayload());
		return new DatagramMessage(getCounterpartAddress(), datagramMessagePayload);
	}

}