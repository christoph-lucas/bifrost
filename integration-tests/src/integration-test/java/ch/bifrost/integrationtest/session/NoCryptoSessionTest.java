package ch.bifrost.integrationtest.session;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.core.api.config.BifrostConfiguration;
import ch.bifrost.core.api.config.SessionConverterConfiguration.SessionConverterType;
import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.core.impl.MessageCodecUtils;

public class NoCryptoSessionTest extends AbstractSessionTest {

	private static final Logger LOG = LoggerFactory.getLogger(NoCryptoSessionTest.class);

	public static final byte[] PING = MessageCodecUtils.encodeStringAsByteArray("ping");
	public static final byte[] PONG = MessageCodecUtils.encodeStringAsByteArray("pong");

	protected BifrostConfiguration getConfig () {
		BifrostConfiguration config = super.getConfig();
		config.sessionConverter().type(SessionConverterType.NO_CRYPTO);
		return config;
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
