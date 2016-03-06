package ch.bifrost.core.api.session;

import ch.bifrost.core.impl.session.SingleSessionEndpoint;

/**
 * A factory that produces {@link SessionLayerAdapter}s.
 */
public interface SessionLayerAdapterFactory<T extends SessionLayerAdapter> {

	/**
	 * Create a new {@link SessionLayerAdapter}.
	 * @param singleSessionEndpoint to be used
	 * @return a new {@link SessionLayerAdapter}
	 */
	T newSessionLayerAdapter(SingleSessionEndpoint singleSessionEndpoint);
	
}
