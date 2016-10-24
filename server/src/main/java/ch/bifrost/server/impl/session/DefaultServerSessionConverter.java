package ch.bifrost.server.impl.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.core.impl.session.defaultImpl.DataPayloadHandler;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerConverter;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerMessageHandler;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerMessageIdentifier;
import ch.bifrost.core.impl.session.defaultImpl.RekeyHandler;

/**
 * The server side adapter for the default session layer.
 */
public class DefaultServerSessionConverter extends DefaultSessionLayerConverter {

	public DefaultServerSessionConverter (DatagramEndpoint endpoint, IdKeyPair key) {
		super(endpoint);
	}

	@Override
	protected Map<DefaultSessionLayerMessageIdentifier, DefaultSessionLayerMessageHandler> getMessageHandlers (DefaultSessionLayerMessageSender sender,
			BlockingQueue<SessionMessage> queueTowardsUpperLayer) {
		Map<DefaultSessionLayerMessageIdentifier, DefaultSessionLayerMessageHandler> handlers = new HashMap<>();
		handlers.put(DefaultSessionLayerMessageIdentifier.DATA_PAYLOAD, new DataPayloadHandler(queueTowardsUpperLayer));
		handlers.put(DefaultSessionLayerMessageIdentifier.CONTROL_REKEY, new RekeyHandler(sender));
		return handlers;
	}

	public static class DefaultServerSessionConverterFactory implements SessionConverterFactory {

		@Override
		public DefaultServerSessionConverter newSessionConverter (DatagramEndpoint endpoint, IdKeyPair key) {
			return new DefaultServerSessionConverter(endpoint, key);
		}
	}

}
