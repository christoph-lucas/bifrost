package ch.bifrost.integrationtest.session;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.client.impl.session.DefaultClientSessionConverter;
import ch.bifrost.client.impl.session.DefaultClientSessionConverter.DefaultClientSessionConverterFactory;
import ch.bifrost.core.api.session.Message;
import ch.bifrost.server.impl.session.DefaultServerSessionConverter.DefaultServerSessionConverterFactory;

public class DefaultSessionTest extends AbstractSessionTest {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionTest.class);
	
    protected DefaultServerSessionConverterFactory getServerSessionConverterFactory() {
		return new DefaultServerSessionConverterFactory();
	}

	protected DefaultClientSessionConverterFactory getClientSessionConverterFactory() {
		return new DefaultClientSessionConverterFactory();
	}

	@Test
	public void shouldPlayPingPongOnce() throws Exception {
		client().initializeSession(1000, TimeUnit.SECONDS);
		LOG.info("Client session initialized");
		client().send(new Message("ping"));
		LOG.info(client().receive().toString());
	}
	
	@Test
	public void shouldPlayPingPongTwice() throws Exception {
		client().initializeSession(1000, TimeUnit.SECONDS);
		client().send(new Message("ping"));
		LOG.info(client().receive().toString());
		client().send(new Message("pong"));
		LOG.info(client().receive().toString());
	}
	
	@Test
	public void shouldPingAndRekey() throws Exception {
		DefaultClientSessionConverter clientConverter = (DefaultClientSessionConverter) client().initializeSession(1000, TimeUnit.SECONDS);
		client().send(new Message("ping"));
		LOG.info(client().receive().toString());
		clientConverter.rekey();
	}

}
