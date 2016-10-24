package ch.bifrost.core.impl.session.defaultImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.core.impl.MessageCodecUtils;

/**
 * Handles Rekey Reply Messages.
 */
public class RekeyReplyHandler implements MessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RekeyReplyHandler.class);

	@Override
	public void handle (Message message) {
		LOG.debug("Received rekey reply with content: " + MessageCodecUtils.decodeStringFromByteArray(message.getPayload()));
	}

}
