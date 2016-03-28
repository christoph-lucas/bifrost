package ch.bifrost.core.api.session;

import ch.bifrost.core.impl.session.NetworkEndointForSessionConverter;

/**
 * A factory that produces {@link SessionConverter}s.
 */
public interface SessionConverterFactory {

	/**
	 * Create a new {@link SessionConverter}.
	 * @param networkAccessPoint to be used
	 * @return a new {@link SessionConverter}
	 */
	SessionConverter newSessionConverter(NetworkEndointForSessionConverter networkAccessPoint, IdKeyPair key);
	
}
