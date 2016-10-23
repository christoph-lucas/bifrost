package ch.bifrost.integrationtest.session;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.core.impl.MessageCodecUtils;
import ch.bifrost.core.impl.session.NoCryptoSessionConverter.NoCryptoSessionConverterFactory;

public class NoCryptoSessionTest extends AbstractSessionTest {

	private static final Logger LOG = LoggerFactory.getLogger(NoCryptoSessionTest.class);

	public static final byte[] PING = MessageCodecUtils.encodeStringAsByteArray("ping");
	public static final byte[] PONG = MessageCodecUtils.encodeStringAsByteArray("pong");

	protected NoCryptoSessionConverterFactory getServerSessionConverterFactory () {
		return new NoCryptoSessionConverterFactory();
	}

	protected NoCryptoSessionConverterFactory getClientSessionConverterFactory () {
		return new NoCryptoSessionConverterFactory();
	}

	@Test
	public void shouldPlayPingPongOnce () throws Exception {
		client().initializeSession(1000, TimeUnit.SECONDS);
		client().send(new SessionMessage(PING));
		LOG.info(client().receive().toString());
	}

	@Test
	public void shouldPlayPingPongTwice () throws Exception {
		client().initializeSession(1000, TimeUnit.SECONDS);
		client().send(new SessionMessage(PING));
		LOG.info(client().receive().toString());
		client().send(new SessionMessage(PONG));
		LOG.info(client().receive().toString());
	}

}
