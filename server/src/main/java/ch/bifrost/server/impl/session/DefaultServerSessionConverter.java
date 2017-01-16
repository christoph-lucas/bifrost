package ch.bifrost.server.impl.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.core.impl.session.defaultImpl.DataPayloadHandler;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionConverter;
import ch.bifrost.core.impl.session.defaultImpl.MessageHandler;
import ch.bifrost.core.impl.session.defaultImpl.MessageIdentifier;
import ch.bifrost.core.impl.session.defaultImpl.RekeyHandler;

/**
 * The server side adapter for the default session layer.
 */
public class DefaultServerSessionConverter extends DefaultSessionConverter {

	@Inject
	public DefaultServerSessionConverter (@Assisted DatagramEndpoint endpoint, @Assisted IdKeyPair key) {
		super(endpoint);
	}

	@Override
	protected Map<MessageIdentifier, MessageHandler> getMessageHandlers (DefaultSessionLayerMessageSender sender,
			BlockingQueue<SessionMessage> queueTowardsUpperLayer) {
		Map<MessageIdentifier, MessageHandler> handlers = new HashMap<>();
		handlers.put(MessageIdentifier.DATA_PAYLOAD, new DataPayloadHandler(queueTowardsUpperLayer));
		handlers.put(MessageIdentifier.CONTROL_REKEY, new RekeyHandler(sender));
		return handlers;
	}

	public static interface DefaultServerSessionConverterFactory extends SessionConverterFactory {
		DefaultServerSessionConverter create(DatagramEndpoint networkAccessPoint, IdKeyPair key);
	}

}
