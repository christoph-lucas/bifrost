package ch.bifrost.core.impl.session.defaultImpl;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.core.api.session.Message;

/**
 * A {@link DefaultSessionLayerMessageHandler} that simply puts the received message into the queue (for now).
 */
public class DataPayloadHandler implements DefaultSessionLayerMessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DataPayloadHandler.class);
	private BlockingQueue<Message> queue;

	public DataPayloadHandler(BlockingQueue<Message> queue) {
		this.queue = queue;
	}
	
	@Override
	public void handle(DefaultSessionLayerMessage message) {
		LOG.debug("Received message with payload. Decrypting and queueing for upper layer.");
		try {
			queue.put(new Message(message.getPayload()));
		} catch (InterruptedException e) {
			LOG.error("Cannot queue message.");
		}
	}

}
