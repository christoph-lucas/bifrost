package ch.bifrost.server.impl.server;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionMessage;
import ch.bifrost.server.api.server.ServerProcess;
import ch.bifrost.server.api.server.ServerProcessFactory;

/**
 * A very simple server that just echos back whatever it receives.
 */
public class EchoServer implements ServerProcess {

    private static final Logger LOG = LoggerFactory.getLogger(EchoServer.class);
	
	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
	
	private SessionConverter sessionEndpoint;
	private boolean cancelled;

	public EchoServer(SessionConverter sessionEndpoint) {
		this.sessionEndpoint = sessionEndpoint;
	}
	
	@Override
	public void run() {
		LOG.debug("Hello from the EchoServer. I'm up and running!");
		while(!cancelled) {
			try {
				Optional<SessionMessage> message = sessionEndpoint.receive(TIMEOUT, TIMEOUT_UNIT);
				if (!message.isPresent()) {
					continue;
				}
				LOG.debug("Server received message, sending echo.");
				sessionEndpoint.send(new SessionMessage(message.get().getPayload()));
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
		public ServerProcess newServerProcess(SessionConverter sessionEndpoint) {
			return new EchoServer(sessionEndpoint);
		}
	}



}
