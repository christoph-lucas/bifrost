package ch.bifrost.core.api.datagram;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;

import org.junit.Test;

public class PacketTest {

	private static final String CONTENT = "content";

	@Test
	public void shouldDecodeWhatWasEncoded() throws Exception {
		String decoded = new Packet((InetAddress) null, 0, CONTENT).getContent();
		assertThat(decoded, is(equalTo(CONTENT)));
	}
}
