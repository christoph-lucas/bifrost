package ch.bifrost.server.api.server;

import ch.bifrost.core.api.session.SessionLayerAdapter;

public interface ServerProcessFactory {

	/**
	 * @return a runnable server process that handles this session
	 */
	ServerProcess newServerProcess(SessionLayerAdapter sessionAdapter);
	
}
