package ch.bifrost.server.impl.server;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

	public static final long TIMEOUT = 100L;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	private static final Logger LOG = LoggerFactory.getLogger(EchoServer.class);

	private static final AtomicInteger processCounter = new AtomicInteger(0);

	private final int id;

	private SessionConverter sessionEndpoint;
	private boolean cancelled;

	public EchoServer (SessionConverter sessionEndpoint) {
		this.id = EchoServer.processCounter.getAndIncrement();
		this.sessionEndpoint = sessionEndpoint;
	}

	@Override
	public String getId () {
		return Integer.toString(this.id);
	}

	@Override
	public void run () {
		LOG.debug("Hello from the EchoServer. I'm up and running!");
		while (!cancelled) {
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
	public void cancel () {
		cancelled = true;
	}

	public static class EchoServerFactory implements ServerProcessFactory {

		@Override
		public ServerProcess newServerProcess (SessionConverter sessionEndpoint) {
			return new EchoServer(sessionEndpoint);
		}
	}

}
