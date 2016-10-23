package ch.bifrost.core.impl.session.defaultImpl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionMessage;

/**
 * The default implementation of the session layer as proposed in the paper. Serves as a base class for both client and server.
 */
public abstract class DefaultSessionLayerConverter implements SessionConverter, Closeable {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionLayerConverter.class);

	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	private final BlockingQueue<SessionMessage> queueTowardsUpperLayer = new LinkedBlockingQueue<>();
	private final DefaultSessionLayerReceiver receiver;
	private DefaultSessionLayerMessageSender sender;

	public DefaultSessionLayerConverter (DatagramEndpoint networkAccessPoint) {
		sender = new DefaultSessionLayerMessageSender(networkAccessPoint);
		receiver = new DefaultSessionLayerReceiver(networkAccessPoint, getMessageHandlers(sender, queueTowardsUpperLayer));
		receiver.start();
	}

	/**
	 * Add handlers for all expected message types. Handlers may differ on server and client side.
	 */
	protected abstract Map<DefaultSessionLayerMessageIdentifier, DefaultSessionLayerMessageHandler> getMessageHandlers (DefaultSessionLayerMessageSender sender,
			BlockingQueue<SessionMessage> queueTowardsUpperLayer);

	protected DefaultSessionLayerMessageSender getSender () {
		return sender;
	}

	@Override
	public void send (SessionMessage message) throws IOException {
		LOG.debug("Encrypting and sending a message");
		// TODO encrypt message
		DefaultSessionLayerMessage convertedMessage = new DefaultSessionLayerMessage(DefaultSessionLayerMessageIdentifier.DATA_PAYLOAD, message.getPayload());
		sender.send(convertedMessage);
	}

	@Override
	public SessionMessage receive () throws InterruptedException {
		return queueTowardsUpperLayer.take();
	}

	@Override
	public Optional<SessionMessage> receive (long timeout, TimeUnit unit) throws InterruptedException {
		SessionMessage msg = queueTowardsUpperLayer.poll(TIMEOUT, TIMEOUT_UNIT);
		if (msg != null) {
			return Optional.of(msg);
		}
		return Optional.absent();
	}

	@Override
	public void close () throws IOException {
		receiver.cancel();
	}

	public static class DefaultSessionLayerMessageSender {

		private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionLayerMessageSender.class);

		private final DatagramEndpoint endpoint;

		public DefaultSessionLayerMessageSender (DatagramEndpoint networkAccessPoint) {
			this.endpoint = networkAccessPoint;
		}

		public void send (DefaultSessionLayerMessage message) throws IOException {
			LOG.debug("Received a message to be sent.");
			endpoint.send(message.toSessionMessage().toDatagramMessage());
		}

	}

}
