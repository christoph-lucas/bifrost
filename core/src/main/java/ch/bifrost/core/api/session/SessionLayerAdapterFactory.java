package ch.bifrost.core.api.session;

import ch.bifrost.core.impl.session.SessionAdapterNetworkAccessPoint;

/**
 * A factory that produces {@link SessionLayerAdapter}s.
 */
public interface SessionLayerAdapterFactory<T extends SessionLayerAdapter> {

	/**
	 * Create a new {@link SessionLayerAdapter}.
	 * @param networkAccessPoint to be used
	 * @return a new {@link SessionLayerAdapter}
	 */
	T newSessionLayerAdapter(SessionAdapterNetworkAccessPoint networkAccessPoint);
	
}
