package ch.bifrost.core.impl.session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.IdKeyPair;
import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionConverterFactory;

/**
 * A very simple {@link SessionConverter} that just passes all messages through without any Crypto or other processing.
 */
public class NoCryptoSessionConverter implements SessionConverter {

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
		return networkAccessPoint.receive();
	}

	@Override
	public Optional<SessionMessage> receive(long timeout, TimeUnit unit) throws Exception {
		return networkAccessPoint.receive(timeout, unit);
	}

	public static class NoCryptoSessionConverterFactory implements SessionConverterFactory {
		
		@Override
		public NoCryptoSessionConverter newSessionConverter(NetworkEndointForSessionConverter networkAccessPoint, IdKeyPair key) {
			return new NoCryptoSessionConverter(networkAccessPoint);
		}
	}

}
