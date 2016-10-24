package ch.bifrost.core.impl.session.defaultImpl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bifrost.core.impl.MessageCodecUtils;
import ch.bifrost.core.impl.session.defaultImpl.DefaultSessionLayerConverter.DefaultSessionLayerMessageSender;

/**
 * Handles rekey messages.
 */
public class RekeyHandler implements MessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RekeyHandler.class);

	private DefaultSessionLayerMessageSender sender;

	public RekeyHandler (DefaultSessionLayerMessageSender sender) {
		this.sender = sender;
	}

	@Override
	public void handle (Message message) throws IOException {
		LOG.debug("Received Rekey Message");
		// for now not much
		byte[] messageBytes = MessageCodecUtils.encodeStringAsByteArray("I did a rekeying as asked to do.");
		sender.send(new Message(MessageIdentifier.CONTROL_REKEY_REPLY, messageBytes));
	}

}
