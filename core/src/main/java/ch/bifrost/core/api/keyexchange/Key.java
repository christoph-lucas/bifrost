package ch.bifrost.core.api.keyexchange;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Key {

	public static final int MINIMUM_KEY_LENGTH_IN_BYTES = 64; // 512 bits

	private final byte[] key;

	public static Key fromBytes (byte[] keyBytes) {
		if (keyBytes.length < MINIMUM_KEY_LENGTH_IN_BYTES) {
			throw new IllegalArgumentException("Provided key is too short. Expected at least " + MINIMUM_KEY_LENGTH_IN_BYTES + " bytes, got " + keyBytes.length);
		}
		// only use the least significant 512 bits
		byte[] leastSignificantBytes = ArrayUtils.subarray(keyBytes, keyBytes.length - MINIMUM_KEY_LENGTH_IN_BYTES, keyBytes.length);
		return new Key(leastSignificantBytes);
	}

	public byte[] toBytes () {
		return Arrays.clone(key);
	}

}
