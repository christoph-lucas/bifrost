package ch.bifrost.core.impl.session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.session.IdKeyPair;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.api.session.SessionMessage;

/**
 * A very simple {@link SessionConverter} that just passes all messages through without any Crypto or other processing.
 */
public class NoCryptoSessionConverter implements SessionConverter {

	private static final Logger LOG = LoggerFactory.getLogger(NoCryptoSessionConverter.class);

	private NetworkEndointForSessionConverter networkAccessPoint;

	public NoCryptoSessionConverter(NetworkEndointForSessionConverter networkAccessPoint) {
		this.networkAccessPoint = networkAccessPoint;
	}
	
	@Override
	public void send(SessionMessage message) throws IOException {
		networkAccessPoint.send(message);
	}

	@Override
	public SessionMessage receive() throws Exception {
		SessionMessage sessionMessage = networkAccessPoint.receive();
		LOG.debug("Received a message. Updating Counterpart Address on network endpoint.");
		networkAccessPoint.counterpartAddress((CounterpartAddress) sessionMessage.getContextData().get(SessionMessage.COUNTERPART_ADDRESS)); 
		return sessionMessage;
	}

	@Override
	public Optional<SessionMessage> receive(long timeout, TimeUnit unit) throws Exception {
		Optional<SessionMessage> sessionMessage = networkAccessPoint.receive(timeout, unit);
		LOG.debug("Received a message. Updating Counterpart Address on network endpoint.");
		networkAccessPoint.counterpartAddress((CounterpartAddress) sessionMessage.get().getContextData().get(SessionMessage.COUNTERPART_ADDRESS)); 
		return sessionMessage;
	}

	public static class NoCryptoSessionConverterFactory implements SessionConverterFactory {
		
		@Override
		public NoCryptoSessionConverter newSessionConverter(NetworkEndointForSessionConverter networkAccessPoint, IdKeyPair key) {
			return new NoCryptoSessionConverter(networkAccessPoint);
		}
	}

}
