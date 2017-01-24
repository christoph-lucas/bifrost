package ch.bifrost.core.api.keyexchange;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import ch.bifrost.core.api.datagram.MultiplexingID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DHKeyExchangeResponseMessage {

	private BigInteger serverPublicKey;
	private MultiplexingID id;

	public static DHKeyExchangeResponseMessage from (byte[] payload) {
		byte[] idBytes = Arrays.copyOfRange(payload, 0, MultiplexingID.LENGTH_IN_BYTES);
		ch.bifrost.core.api.datagram.MultiplexingID id = MultiplexingID.fromBytes(idBytes);

		byte[] keyBytes = Arrays.copyOfRange(payload, MultiplexingID.LENGTH_IN_BYTES, payload.length);
		BigInteger publicKey = new BigInteger(keyBytes);

		return new DHKeyExchangeResponseMessage(publicKey, id);
	}

	public byte[] toByteArray () {
		byte[] idBytes = id.toBytes();
		byte[] keyBytes = serverPublicKey.toByteArray();
		return ArrayUtils.addAll(idBytes, keyBytes);
	}

}
