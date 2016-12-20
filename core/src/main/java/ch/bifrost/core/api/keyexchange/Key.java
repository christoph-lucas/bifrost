package ch.bifrost.core.api.keyexchange;

import org.bouncycastle.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Key {

	private final byte[] key;

	public static Key fromBytes (byte[] keyBytes) {
		return new Key(Arrays.clone(keyBytes));
	}

	public byte[] toBytes () {
		return Arrays.clone(key);
	}

}
