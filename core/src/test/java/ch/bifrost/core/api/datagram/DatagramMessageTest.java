package ch.bifrost.core.api.datagram;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;

import org.junit.Test;

import ch.bifrost.core.impl.MessageCodecUtils;

public class DatagramMessageTest {

	private static final byte[] CONTENT = MessageCodecUtils.encodeStringAsByteArray("content");

	@Test
	public void shouldDecodeWhatWasEncoded () throws Exception {
		byte[] decoded = new DatagramMessage(new CounterpartAddress((InetAddress) null, 0), CONTENT).getPayload();
		assertThat(decoded, is(equalTo(CONTENT)));
	}
}
