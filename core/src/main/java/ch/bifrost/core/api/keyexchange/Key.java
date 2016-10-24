package ch.bifrost.core.api.keyexchange;

import java.util.concurrent.ThreadLocalRandom;

import org.bouncycastle.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Key {

	public static final int LENGTH_IN_BYTES = 64;

	private final byte[] key;

	public static Key createRandomKey () {
		byte[] keyBytes = new byte[LENGTH_IN_BYTES];
		ThreadLocalRandom.current().nextBytes(keyBytes);
		return new Key(keyBytes);
	}

	public static Key fromBytes (byte[] keyBytes) {
		return new Key(Arrays.clone(keyBytes));
	}

	public byte[] toBytes () {
		return Arrays.clone(key);
	}

}
