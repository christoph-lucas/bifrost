package ch.bifrost.server.impl.session;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;

import com.google.inject.Inject;

/**
 * A helper class that plugs everything together for a server on the session layer.
 */
public class SessionServer implements Closeable {

	private final SessionController server;

	@Inject
	public SessionServer (SessionController controller) throws SocketException {
		server = controller;
		server.start();
	}

	@Override
	public void close () throws IOException {
		server.cancel();
	}

}
