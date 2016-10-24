package ch.bifrost.core.impl.session.defaultImpl;

import lombok.Getter;

/**
 * The different types of messages in the default session layer.
 */
public enum MessageIdentifier {

	DATA_PAYLOAD((byte) 1),
	CONTROL_REKEY((byte) 2),
	CONTROL_REKEY_REPLY((byte) 3);

	public static final int BYTE_REPRESENTATION_LENGTH = 1;

	@Getter
	private final byte[] byteVal;

	private MessageIdentifier (byte byteVal) {
		this.byteVal = new byte[] { byteVal };
	}

	public static MessageIdentifier from (byte[] bytes) {
		byte byteVal = bytes[0];

		switch (byteVal) {
		case 1:
			return MessageIdentifier.DATA_PAYLOAD;
		case 2:
			return MessageIdentifier.CONTROL_REKEY;
		case 3:
			return MessageIdentifier.CONTROL_REKEY_REPLY;
		default:
			throw new IllegalArgumentException("The given byte array are invalid");
		}
	}
}
