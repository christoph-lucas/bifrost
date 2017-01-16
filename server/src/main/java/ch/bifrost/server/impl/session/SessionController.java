package ch.bifrost.server.impl.session;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.MultiplexingID;
import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.server.api.server.ServerProcess;
import ch.bifrost.server.api.server.ServerProcessFactory;
import ch.bifrost.server.impl.datagram.DatagramEndpointMultiplexer;
import ch.bifrost.server.impl.keyexchange.KeyExchangeServer;

public class SessionController extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(SessionController.class);

	private static final int NUM_THREADS = 10;
	private static final long TIMEOUT = 100L;
	private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	private SessionConverterFactory sessionConverterFactory;
	private ServerProcessFactory serverFactory;
	private KeyExchangeServer keyExchange;
	private DatagramEndpointMultiplexer multiplexer;
	private final ServerProcessStore sessionStore = new ServerProcessStore();

	private boolean cancelled;
	private ExecutorService threadPool;

	@Inject
	public SessionController (KeyExchangeServer keyExchange,
			DatagramEndpointMultiplexer multiplexer,
			SessionConverterFactory sessionConverterFactory,
			ServerProcessFactory serverFactory) {
		this.keyExchange = keyExchange;
		this.multiplexer = multiplexer;
		this.sessionConverterFactory = sessionConverterFactory;
		this.serverFactory = serverFactory;
		threadPool = Executors.newFixedThreadPool(NUM_THREADS);
	}

	@Override
	public void run () {
		MultiplexingID nextId = MultiplexingID.createRandomId();
		LOG.debug("Created random id '{}'.", nextId);
		DatagramEndpoint singleSessionEndpoint = multiplexer.registerSessionID(nextId);

		while (!cancelled) {
			Optional<IdKeyPair> generatedKey = Optional.absent();
			try {
				generatedKey = keyExchange.get(nextId, TIMEOUT, TIMEOUT_UNIT);
			} catch (Exception e) {
				LOG.error("Error occurred during Key Exchange: " + e.getMessage(), e);
				continue;
			}
			if (!generatedKey.isPresent()) {
				continue;
			}
			IdKeyPair idKeyPair = generatedKey.get();
			if (!nextId.equals(idKeyPair.getId())) {
				LOG.error("Generated ID and next ID are not equal, aborting.");
				continue;
			}
			LOG.debug("Received new Key with ID: " + idKeyPair.getId());
			SessionConverter sessionconverter = sessionConverterFactory.create(singleSessionEndpoint, idKeyPair);
			ServerProcess newServerProcess = serverFactory.newServerProcess(sessionconverter);
			sessionStore.put(newServerProcess);
			threadPool.submit(newServerProcess);

			nextId = MultiplexingID.createRandomId();
			LOG.debug("Created random id '{}'.", nextId);
			singleSessionEndpoint = multiplexer.registerSessionID(nextId);
		}
	}

	public void cancel () throws IOException {
		cancelled = true;
		sessionStore.killAll();
		keyExchange.close();
		multiplexer.close();
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			// ignore
		}
		threadPool.shutdownNow();
	}
}
