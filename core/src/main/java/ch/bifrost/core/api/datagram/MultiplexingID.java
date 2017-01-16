package ch.bifrost.core.api.datagram;

import java.util.concurrent.ThreadLocalRandom;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiplexingID {

	public static final int LENGTH_IN_BYTES = 32;

	private final byte[] id;

	public static MultiplexingID createRandomId () {
		byte[] idBytes = new byte[LENGTH_IN_BYTES];
		ThreadLocalRandom.current().nextBytes(idBytes);
		return new MultiplexingID(idBytes);
	}

	public static MultiplexingID fromBytes (byte[] idAsByte) {
		return new MultiplexingID(Arrays.clone(idAsByte));
	}

	public byte[] toBytes () {
		return Arrays.clone(id);
	}

	@Override
	public String toString () {
		return Base64.toBase64String(id);
	}
}
