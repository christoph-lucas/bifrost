package ch.bifrost.client.impl.keyexchange;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import ch.bifrost.core.api.keyexchange.IdKeyPair;

public interface KeyExchangeClient extends Closeable {

	Optional<IdKeyPair> get (long timeout, TimeUnit unit) throws Exception;

}