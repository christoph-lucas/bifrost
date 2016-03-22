package ch.bifrost.server.impl.session;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.server.api.server.ServerProcess;

/**
 * Stores all sessions with an attached state.
 */
public class SessionStore {

	private ConcurrentHashMap<String, SessionState> store = new ConcurrentHashMap<>();
	
	public boolean contains(String id) {
		return store.containsKey(id);
	}
	
	public void put(String id, BlockingQueue<SessionPacket> packetReceiverQueue, ServerProcess process) {
		store.put(id, SessionState.builder()
				.id(id)
				.receivedPackages(packetReceiverQueue)
				.serverProcess(process)
				.alive(true)
				.build());
	}

	public SessionState get(String id) {
		return store.get(id);
	}
	
	public void kill(String id) {
		store.get(id).kill();
	}
	
	public void killAll() {
		store.forEach((id, state) -> state.getServerProcess().cancel());
	}
	
}
