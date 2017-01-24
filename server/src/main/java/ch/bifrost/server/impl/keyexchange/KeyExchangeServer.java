package ch.bifrost.server.impl.keyexchange;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.MultiplexingID;
import ch.bifrost.core.api.keyexchange.IdKeyPair;

public interface KeyExchangeServer extends Closeable {

	Optional<IdKeyPair> get (MultiplexingID id, long timeout, TimeUnit unit) throws Exception;

}