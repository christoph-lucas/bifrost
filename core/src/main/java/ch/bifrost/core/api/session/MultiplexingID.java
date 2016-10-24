package ch.bifrost.core.api.session;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import ch.bifrost.core.impl.MessageCodecUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiplexingID {

	public static final int LENGTH_IN_BYTES = 32;

	private final String id;

	public static MultiplexingID createRandomId () {
		String id = ThreadLocalRandom.current()
				.ints(0, 9)
				.limit(LENGTH_IN_BYTES)
				.mapToObj(Integer::toString)
				.collect(Collectors.joining());
		return new MultiplexingID(id);
	}

	public static MultiplexingID fromBytes (byte[] idAsByte) {
		return new MultiplexingID(MessageCodecUtils.decodeStringFromByteArray(idAsByte));
	}

	public byte[] toBytes () {
		return MessageCodecUtils.encodeStringAsByteArrayWithFixedLength(id, LENGTH_IN_BYTES);
	}

}
