package ch.bifrost.core.impl.session.defaultImpl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.impl.session.SessionAdapterNetworkAccessPoint;

/**
 * A receiver thread within the default session layer. Distinguishes between the different sorts of messages and handles
 * each message according to its identifier using an appropriate handler.
 */
public class DefaultSessionLayerReceiver extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionLayerReceiver.class);

	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	private SessionAdapterNetworkAccessPoint endpoint;
	private boolean cancelled;
	private Map<DefaultSessionLayerMessageIdentifier, DefaultSessionLayerMessageHandler> handlers;
	private final ObjectMapper json = new ObjectMapper();

	public DefaultSessionLayerReceiver(SessionAdapterNetworkAccessPoint endpoint, Map<DefaultSessionLayerMessageIdentifier, DefaultSessionLayerMessageHandler> handlers) {
		this.endpoint = endpoint;
		this.handlers = handlers;
	}
	
	@Override
	public void run() {
		while(!cancelled) {
			Optional<Message> receivedMessage;
			try {
				receivedMessage = endpoint.receive(TIMEOUT, TIMEOUT_UNIT);
			} catch (Exception e) {
				LOG.warn("An exception occurred during receiving a message: " + e.getMessage(), e);
				continue;
			}
			if (!receivedMessage.isPresent()) {
				continue;
			}
			try {
				LOG.debug("Received a message. Converting to DefaultSessionLayerMessage.");
				DefaultSessionLayerMessage sessionLayerMessage = json.readValue(receivedMessage.get().getContent(), DefaultSessionLayerMessage.class);
				LOG.debug("Looking for handler.");
				DefaultSessionLayerMessageHandler handler = handlers.get(sessionLayerMessage.getIdentifier());
				if (handler != null) {
					LOG.debug("Handler found.");
					handler.handle(sessionLayerMessage);
				} else {
					LOG.error("No suitable Message handler found.");
				}
			} catch (IOException e) {
				LOG.error("Cannot decode message.");
			}
		}
	}
	
	public void cancel() {
		cancelled = true;
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	
}