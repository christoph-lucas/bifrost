package ch.bifrost.core.impl.dependencyInjection;

import com.google.inject.AbstractModule;

import ch.bifrost.core.api.config.BifrostConfiguration;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServiceBinder extends AbstractModule {

	private BifrostConfiguration config;

	@Override
	protected void configure () {
		// nothing so far, may contain general configuration
	}

}
