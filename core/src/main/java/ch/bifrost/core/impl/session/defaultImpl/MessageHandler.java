package ch.bifrost.core.impl.session.defaultImpl;

import java.io.IOException;

/**
 * An abstraction of a message handler in the default session layer.
 */
public interface MessageHandler {

	void handle (Message receivedMessage) throws IOException;

}
