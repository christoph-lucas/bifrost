package ch.bifrost.core.impl.session.defaultImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles Rekey Reply Messages.
 */
public class RekeyReplyHandler implements DefaultSessionLayerMessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RekeyReplyHandler.class);

	@Override
	public void handle(DefaultSessionLayerMessage message) {
		LOG.debug("Received rekey reply with content: " + message.getPayload());
	}

}
