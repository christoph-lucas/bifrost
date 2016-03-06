package ch.bifrost.client.impl.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionLayerAdapterFactory;
import ch.bifrost.core.impl.session.SessionAdapterNetworkAccessPoint;
import ch.bifrost.core.impl.session.defaultImpl.DataPayloadHandler;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerAdapter;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerMessage;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerMessageHandler;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerMessageIdentifier;
import ch.bifrost.core.impl.session.defaultImpl.RekeyReplyHandler;

/**
 * The client side adapter for the default session layer.
 */
public class DefaultSessionLayerClientAdapter extends DefaultSessionLayerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionLayerClientAdapter.class);

	public DefaultSessionLayerClientAdapter(SessionAdapterNetworkAccessPoint networkAccessPoint) {
		super(networkAccessPoint);
	}

	protected Map<DefaultSessionLayerMessageIdentifier, DefaultSessionLayerMessageHandler> getMessageHandlers(DefaultSessionLayerMessageSender sender, BlockingQueue<Message> queueTowardsUpperLayer) {
		Map<DefaultSessionLayerMessageIdentifier, DefaultSessionLayerMessageHandler> handlers = new HashMap<>();
		handlers.put(DefaultSessionLayerMessageIdentifier.DATA_PAYLOAD, new DataPayloadHandler(queueTowardsUpperLayer));
		handlers.put(DefaultSessionLayerMessageIdentifier.CONTROL_REKEY_REPLY, new RekeyReplyHandler());
		return handlers;
	}

	/**
	 * Issue a rekeying request to the server.
	 */
	public void rekey() throws IOException {
		LOG.debug("Sending rekey message to server");
		getSender().send(new DefaultSessionLayerMessage(DefaultSessionLayerMessageIdentifier.CONTROL_REKEY, "Whatever..."));
	}
	
	public static class DefaultSessionLayerClientAdapterFactory implements SessionLayerAdapterFactory<DefaultSessionLayerClientAdapter> {
		
		@Override
		public DefaultSessionLayerClientAdapter newSessionLayerAdapter(SessionAdapterNetworkAccessPoint networkAccessPoint) {
			return new DefaultSessionLayerClientAdapter(networkAccessPoint);
		}
	}

}
