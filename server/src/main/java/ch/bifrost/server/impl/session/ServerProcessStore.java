package ch.bifrost.server.impl.session;

import java.util.concurrent.ConcurrentHashMap;

import ch.bifrost.server.api.server.ServerProcess;

/**
 * Stores all sessions with an attached state.
 */
public class ServerProcessStore {

	private ConcurrentHashMap<String, ServerProcessState> store = new ConcurrentHashMap<>();

	public void put (ServerProcess serverProcess) {
		store.put(serverProcess.getId(), ServerProcessState.builder()
				.serverProcess(serverProcess)
				.alive(true)
				.build());
	}

	public void kill (String id) {
		store.get(id).kill();
	}

	public void killAll () {
		store.forEach( (id, state) -> state.kill());
	}

}
