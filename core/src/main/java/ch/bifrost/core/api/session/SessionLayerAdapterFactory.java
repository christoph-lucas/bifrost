package ch.bifrost.core.api.session;

import ch.bifrost.core.impl.session.SingleSessionEndpoint;

/**
 * A factory that produces {@link SessionLayerAdapter}s.
 */
public interface SessionLayerAdapterFactory {

	/**
	 * Create a new {@link SessionLayerAdapter}.
	 * @param singleSessionEndpoint to be used
	 * @return a new {@link SessionLayerAdapter}
	 */
	SessionLayerAdapter newSessionLayerAdapter(SingleSessionEndpoint singleSessionEndpoint);
	
}
