package ch.bifrost.core.impl;

import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.base.Charsets;

public class MessageCodecUtils {

	public static final Charset CHARSET = Charsets.UTF_8;

	public static byte[] encodeStringAsByteArray (String s) {
		return s.getBytes(CHARSET);
	}

	public static byte[] encodeStringAsByteArrayWithFixedLength (String s, int length) {
		byte[] bytes = encodeStringAsByteArray(s);
		// TODO rather throw exception if string does not have correct length?
		byte[] bytesCorrectLength = Arrays.copyOf(bytes, length);
		return bytesCorrectLength;
	}

	public static String decodeStringFromByteArray (byte[] sessionIdBytes) {
		return new String(sessionIdBytes, CHARSET);
	}

}
