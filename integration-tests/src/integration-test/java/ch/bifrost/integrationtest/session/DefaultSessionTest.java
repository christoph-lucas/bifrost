package ch.bifrost.integrationtest.session;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.client.impl.session.DefaultClientSessionConverter;
import ch.bifrost.core.api.config.BifrostConfiguration;
import ch.bifrost.core.api.config.SessionConverterConfiguration.SessionConverterType;
import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.core.impl.MessageCodecUtils;

public class DefaultSessionTest extends AbstractSessionTest {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionTest.class);

	public static final byte[] PING = MessageCodecUtils.encodeStringAsByteArray("ping");
	public static final byte[] PONG = MessageCodecUtils.encodeStringAsByteArray("pong");

	protected BifrostConfiguration getConfig () {
		BifrostConfiguration config = super.getConfig();
		config.sessionConverter().type(SessionConverterType.DEFAULT);
		return config;
	}

	@Test
	public void shouldPlayPingPongOnce () throws Exception {
		client().initializeSession(1000, TimeUnit.SECONDS);
		LOG.info("Client session initialized");

		client().send(new SessionMessage(PING));
		SessionMessage message = client().receive();
		LOG.info("---------> Client received a message: '" + MessageCodecUtils.decodeStringFromByteArray(message.getPayload()) + "'");
		assertThat(message.getPayload(), is(equalTo(PING)));
	}

	@Test
	public void shouldPlayPingPongTwice () throws Exception {
		client().initializeSession(1000, TimeUnit.SECONDS);

		client().send(new SessionMessage(PING));
		SessionMessage message = client().receive();
		LOG.info("---------> Client received a message: '" + MessageCodecUtils.decodeStringFromByteArray(message.getPayload()) + "'");
		assertThat(message.getPayload(), is(equalTo(PING)));

		client().send(new SessionMessage(PONG));
		message = client().receive();
		LOG.info("---------> Client received a message: '" + MessageCodecUtils.decodeStringFromByteArray(message.getPayload()) + "'");
		assertThat(message.getPayload(), is(equalTo(PONG)));
	}

	@Test
	public void shouldPingAndRekey () throws Exception {
		DefaultClientSessionConverter clientConverter = (DefaultClientSessionConverter) client().initializeSession(1000, TimeUnit.SECONDS);

		client().send(new SessionMessage(PING));
		SessionMessage message = client().receive();
		LOG.info("---------> Client received a message: '" + MessageCodecUtils.decodeStringFromByteArray(message.getPayload()) + "'");
		assertThat(message.getPayload(), is(equalTo(PING)));

		clientConverter.rekey();
	}

}
