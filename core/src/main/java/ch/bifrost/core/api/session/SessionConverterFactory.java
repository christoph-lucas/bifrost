package ch.bifrost.core.api.session;

import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.keyexchange.IdKeyPair;

/**
 * A factory that produces {@link SessionConverter}s.
 */
public interface SessionConverterFactory {

	/**
	 * Create a new {@link SessionConverter}.
	 * 
	 * @param networkAccessPoint to be used
	 * @return a new {@link SessionConverter}
	 */
	SessionConverter create (DatagramEndpoint networkAccessPoint, IdKeyPair key);

}
