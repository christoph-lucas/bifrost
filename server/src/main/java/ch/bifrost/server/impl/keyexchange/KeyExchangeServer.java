package ch.bifrost.server.impl.keyexchange;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.keyexchange.IdKeyPair;
import ch.bifrost.core.api.session.MultiplexingID;

public interface KeyExchangeServer {

	Optional<IdKeyPair> get (MultiplexingID id, long timeout, TimeUnit unit) throws Exception;

}