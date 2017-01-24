package ch.bifrost.server.impl.session;

import ch.bifrost.server.api.server.ServerProcess;
import lombok.Builder;
import lombok.Data;

/**
 * Represent the state of a session.
 */
@Data
@Builder
public class ServerProcessState {

	private ServerProcess serverProcess;
	private boolean alive;

	public void kill () {
		alive = false;
		if (serverProcess != null) {
			serverProcess.cancel();
		}
	}
}
