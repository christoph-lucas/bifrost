package ch.bifrost.server.impl.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionLayerAdapterFactory;
import ch.bifrost.core.impl.session.SessionAdapterNetworkAccessPoint;
import ch.bifrost.core.impl.session.defaultImpl.DataPayloadHandler;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerAdapter;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerMessageHandler;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerMessageIdentifier;
import ch.bifrost.core.impl.session.defaultImpl.RekeyHandler;

/**
 * The server side adapter for the default session layer.
 */
public class DefaultSessionLayerServerAdapter extends DefaultSessionLayerAdapter {

	public DefaultSessionLayerServerAdapter(SessionAdapterNetworkAccessPoint endpoint) {
		super(endpoint);
	}

	protected Map<DefaultSessionLayerMessageIdentifier, DefaultSessionLayerMessageHandler> getMessageHandlers(DefaultSessionLayerMessageSender sender, BlockingQueue<Message> queueTowardsUpperLayer) {
		Map<DefaultSessionLayerMessageIdentifier, DefaultSessionLayerMessageHandler> handlers = new HashMap<>();
		handlers.put(DefaultSessionLayerMessageIdentifier.DATA_PAYLOAD, new DataPayloadHandler(queueTowardsUpperLayer));
		handlers.put(DefaultSessionLayerMessageIdentifier.CONTROL_REKEY, new RekeyHandler(sender));
		return handlers;
	}

	public static class DefaultSessionLayerServerAdapterFactory implements SessionLayerAdapterFactory<DefaultSessionLayerServerAdapter> {
		
		@Override
		public DefaultSessionLayerServerAdapter newSessionLayerAdapter(SessionAdapterNetworkAccessPoint endpoint) {
			return new DefaultSessionLayerServerAdapter(endpoint);
		}
	}


}
