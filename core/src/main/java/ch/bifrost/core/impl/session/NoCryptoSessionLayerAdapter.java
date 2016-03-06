package ch.bifrost.core.impl.session;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionLayerAdapter;
import ch.bifrost.core.api.session.SessionLayerAdapterFactory;
import ch.bifrost.core.api.session.SessionPacket;

/**
 * A very simple {@link SessionLayerAdapter} that just passes all messages through without any Crypto or other processing.
 */
public class NoCryptoSessionLayerAdapter implements SessionLayerAdapter {

	private SingleSessionEndpoint endpoint;

	public NoCryptoSessionLayerAdapter(SingleSessionEndpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	@Override
	public void send(Message message) throws IOException {
		endpoint.send(message);
	}

	@Override
	public Message receive() throws InterruptedException {
		return endpoint.receive();
	}

	@Override
	public Optional<Message> receive(long timeout, TimeUnit unit) throws InterruptedException {
		return endpoint.receive(timeout, unit);
	}

	@Override
	public String computeId(SessionPacket firstMessage) {
		return ThreadLocalRandom.current().ints(0, 9).limit(30).mapToObj(Integer::toString).collect(Collectors.joining());
	}
	
	public static class NoCryptoSessionAdapterFactory implements SessionLayerAdapterFactory<NoCryptoSessionLayerAdapter> {
		
		@Override
		public NoCryptoSessionLayerAdapter newSessionLayerAdapter(SingleSessionEndpoint endpoint) {
			return new NoCryptoSessionLayerAdapter(endpoint);
		}
	}

}
