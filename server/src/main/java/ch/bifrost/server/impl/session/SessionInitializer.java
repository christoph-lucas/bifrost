package ch.bifrost.server.impl.session;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import ch.bifrost.core.api.session.IdKeyPair;
import ch.bifrost.core.api.session.KeyExchange;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.impl.session.NetworkEndointForSessionConverter;
import ch.bifrost.server.api.server.ServerProcess;
import ch.bifrost.server.api.server.ServerProcessFactory;

public class SessionInitializer extends Thread {
		
	    private static final Logger LOG = LoggerFactory.getLogger(SessionInitializer.class);
	    private static final int NUM_THREADS = 10;
	    private static final long TIMEOUT = 100L;
	    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

		private SessionConverterFactory sessionConverterFactory;
		private ServerProcessFactory serverFactory;
		private KeyExchange keyExchange;
		private SessionMultiplexConverter multiplexer;
	    
		private boolean cancelled;
		private ExecutorService threadPool;

		public SessionInitializer(KeyExchange keyExchange, SessionMultiplexConverter multiplexer, SessionConverterFactory sessionConverterFactory, ServerProcessFactory serverFactory) {
			this.keyExchange = keyExchange;
			this.multiplexer = multiplexer;
			this.sessionConverterFactory = sessionConverterFactory;
			this.serverFactory = serverFactory;
			threadPool = Executors.newFixedThreadPool(NUM_THREADS);
		}
		
		@Override
		public void run() {
			while(!cancelled) {
				Optional<IdKeyPair> generatedKey = Optional.absent();
				try {
					generatedKey = keyExchange.get(TIMEOUT, TIMEOUT_UNIT);
				} catch (Exception e) {
					continue;
				}
				if (!generatedKey.isPresent()) {
					continue;
				}
				
				IdKeyPair idKeyPair = generatedKey.get();
				LOG.debug("Received new Key with ID: " + idKeyPair.getId());
				NetworkEndointForSessionConverter singleSessionEndpoint = multiplexer.registerSessionID(idKeyPair.getId());
				SessionConverter sessionconverter = sessionConverterFactory.newSessionConverter(singleSessionEndpoint, idKeyPair);
				ServerProcess newServerProcess = serverFactory.newServerProcess(sessionconverter);
				multiplexer.getSession(idKeyPair.getId()).setServerProcess(newServerProcess);
				threadPool.submit(newServerProcess);
			}
		}
		
		public void cancel() {
			cancelled = true;
			multiplexer.close();
			try {
				Thread.sleep(TIMEOUT);
			} catch (InterruptedException e) {
				// ignore
			}
			threadPool.shutdownNow();
		}
}
	