package ch.bifrost.client.impl.keyexchange;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.keyexchange.IdKeyPair;

public interface KeyExchangeClient {

	Optional<IdKeyPair> get (long timeout, TimeUnit unit) throws Exception;

}