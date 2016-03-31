package ch.bifrost.integrationtest.session;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.impl.session.NoCryptoSessionConverter.NoCryptoSessionConverterFactory;

public class NoCryptoSessionTest extends AbstractSessionTest {

    private static final Logger LOG = LoggerFactory.getLogger(NoCryptoSessionTest.class);

    protected NoCryptoSessionConverterFactory getServerSessionConverterFactory() {
		return new NoCryptoSessionConverterFactory();
	}

	protected NoCryptoSessionConverterFactory getClientSessionConverterFactory() {
		return new NoCryptoSessionConverterFactory();
	}

	@Test
	public void shouldPlayPingPongOnce() throws Exception {
		client().initializeSession(1000, TimeUnit.SECONDS);
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
	
}
