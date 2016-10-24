package ch.bifrost.core.impl.session.defaultImpl;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.core.api.session.SessionMessage;

/**
 * A {@link MessageHandler} that simply puts the received message into the queue (for now).
 */
public class DataPayloadHandler implements MessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DataPayloadHandler.class);
	private BlockingQueue<SessionMessage> queue;

	public DataPayloadHandler (BlockingQueue<SessionMessage> queue) {
		this.queue = queue;
	}

	@Override
	public void handle (Message message) {
		LOG.debug("Received message with payload. Decrypting and queueing for upper layer.");
		try {
			queue.put(new SessionMessage(message.getPayload()));
		} catch (InterruptedException e) {
			LOG.error("Cannot queue message.");
		}
	}

}
