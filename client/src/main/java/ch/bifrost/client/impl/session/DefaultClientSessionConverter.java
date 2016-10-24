package ch.bifrost.client.impl.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.core.impl.MessageCodecUtils;
import ch.bifrost.core.impl.session.defaultImpl.DataPayloadHandler;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerConverter;
import ch.bifrost.core.impl.session.defaultImpl.Message;
import ch.bifrost.core.impl.session.defaultImpl.MessageHandler;
import ch.bifrost.core.impl.session.defaultImpl.MessageIdentifier;
import ch.bifrost.core.impl.session.defaultImpl.RekeyReplyHandler;

/**
 * The client side adapter for the default session layer.
 */
public class DefaultClientSessionConverter extends DefaultSessionLayerConverter {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultClientSessionConverter.class);

	public DefaultClientSessionConverter (DatagramEndpoint networkAccessPoint) {
		super(networkAccessPoint);
	}

	@Override
	protected Map<MessageIdentifier, MessageHandler> getMessageHandlers (DefaultSessionLayerMessageSender sender,
			BlockingQueue<SessionMessage> queueTowardsUpperLayer) {
		Map<MessageIdentifier, MessageHandler> handlers = new HashMap<>();
		handlers.put(MessageIdentifier.DATA_PAYLOAD, new DataPayloadHandler(queueTowardsUpperLayer));
		handlers.put(MessageIdentifier.CONTROL_REKEY_REPLY, new RekeyReplyHandler());
		return handlers;
	}

	/**
	 * Issue a rekeying request to the server.
	 */
	public void rekey () throws IOException {
		LOG.debug("Sending rekey message to server");
		byte[] messageBytes = MessageCodecUtils.encodeStringAsByteArray("Whatever...");
		getSender().send(new Message(MessageIdentifier.CONTROL_REKEY, messageBytes));
	}

	public static class DefaultClientSessionConverterFactory implements SessionConverterFactory {

		@Override
		public DefaultClientSessionConverter newSessionConverter (DatagramEndpoint networkAccessPoint, IdKeyPair key) {
			return new DefaultClientSessionConverter(networkAccessPoint);
		}
	}

}
