package ch.bifrost.server.impl.session;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.Message;
import ch.bifrost.core.api.session.SessionLayerAdapter;
import ch.bifrost.server.api.server.ServerProcess;
import ch.bifrost.server.api.server.ServerProcessFactory;

/**
 * A very simple server that just echos back whatever it receives.
 */
public class EchoServer implements ServerProcess {

    private static final Logger LOG = LoggerFactory.getLogger(EchoServer.class);
	
	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
	
	private SessionLayerAdapter sessionEndpoint;
	private boolean cancelled;

	public EchoServer(SessionLayerAdapter sessionEndpoint) {
		this.sessionEndpoint = sessionEndpoint;
	}
	
	@Override
	public void run() {
		while(!cancelled) {
			try {
				Optional<Message> message = sessionEndpoint.receive(TIMEOUT, TIMEOUT_UNIT);
				if (!message.isPresent()) {
					continue;
				}
				LOG.debug("Server received message, sending echo.");
				sessionEndpoint.send(new Message("Echo: " + message.get().getContent()));
			} catch (Exception e) {
				LOG.debug("Error receiving a message: " + e.getMessage());
			}
		}
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

	public static class EchoServerFactory implements ServerProcessFactory {
		@Override
		public ServerProcess newServerProcess(SessionLayerAdapter sessionEndpoint) {
			return new EchoServer(sessionEndpoint);
		}
	}



}
