package ch.bifrost.core.api.session;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

public interface KeyExchange {

	Optional<IdKeyPair> get(long timeout, TimeUnit unit) throws Exception;
	
}
