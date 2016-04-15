package ch.bifrost.server.impl.session;

import java.util.concurrent.BlockingQueue;

import ch.bifrost.core.api.session.SessionInternalMessage;
import ch.bifrost.server.api.server.ServerProcess;
import lombok.Builder;
import lombok.Data;

/**
 * Represent the state of a session.
 */
@Data
@Builder
public class SessionState {

	private final String id;
	private final BlockingQueue<SessionInternalMessage> receivedPackages;
	private ServerProcess serverProcess;
	private boolean alive;

	public void kill() {
		alive = false;
		if (serverProcess != null) {
			serverProcess.cancel();
		}
	}
}
