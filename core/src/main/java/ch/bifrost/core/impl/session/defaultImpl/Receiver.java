package ch.bifrost.core.impl.session.defaultImpl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.api.session.SessionMessage;

/**
 * A receiver thread within the default session layer. Distinguishes between the different sorts of messages and handles
 * each message according to its identifier using an appropriate handler.
 */
public class Receiver extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	private DatagramEndpoint endpoint;
	private boolean cancelled;
	private Map<MessageIdentifier, MessageHandler> handlers;

	public Receiver (DatagramEndpoint endpoint, Map<MessageIdentifier, MessageHandler> handlers) {
		this.endpoint = endpoint;
		this.handlers = handlers;
	}

	@Override
	public void run () {
		while (!cancelled) {
			Optional<DatagramMessage> datagram;
			try {
				datagram = endpoint.receive(TIMEOUT, TIMEOUT_UNIT);
			} catch (Exception e) {
				LOG.warn("An exception occurred during receiving a message: " + e.getMessage(), e);
				continue;
			}
			if (!datagram.isPresent()) {
				continue;
			}
			try {
				LOG.debug("Received a message. Updating Counterpart Address on network endpoint.");
				SessionMessage message = SessionMessage.from(datagram.get());
				// TODO #19 The counterpart address should be set by the DefaultSessionConverter or by a MessageHandler, not automatically
				endpoint.counterpartAddress((CounterpartAddress) message.getContextData().get(SessionMessage.COUNTERPART_ADDRESS));
				LOG.debug("Converting to DefaultSessionLayerMessage.");
				Message sessionLayerMessage = Message.from(message);
				LOG.debug("Looking for handler.");
				MessageHandler handler = handlers.get(sessionLayerMessage.getIdentifier());
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

	public void cancel () {
		cancelled = true;
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			// ignore
		}
	}

}
