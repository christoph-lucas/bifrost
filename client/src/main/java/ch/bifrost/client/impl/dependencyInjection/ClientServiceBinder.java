package ch.bifrost.client.impl.dependencyInjection;

import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ch.bifrost.client.impl.datagram.ClientMultiplexedDatagramEndpoint;
import ch.bifrost.client.impl.datagram.ClientMultiplexedDatagramEndpoint.ClientMultiplexedDatagramEndpointFactory;
import ch.bifrost.client.impl.keyexchange.DHKeyExchangeClient;
import ch.bifrost.client.impl.keyexchange.KeyExchangeClient;
import ch.bifrost.client.impl.session.DefaultClientSessionConverter.DefaultClientSessionConverterFactory;
import ch.bifrost.core.api.config.BifrostConfiguration;
import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.impl.datagram.UDPDatagramEndpoint;
import ch.bifrost.core.impl.dependencyInjection.CounterpartAddressProvider;
import ch.bifrost.core.impl.dependencyInjection.KeyExchange;
import ch.bifrost.core.impl.dependencyInjection.Payload;
import ch.bifrost.core.impl.dependencyInjection.ServiceBinder;
import ch.bifrost.core.impl.session.NoCryptoSessionConverter;
import ch.bifrost.core.impl.session.NoCryptoSessionConverter.NoCryptoSessionConverterFactory;

public class ClientServiceBinder extends ServiceBinder {

	private BifrostConfiguration config;

	public ClientServiceBinder (BifrostConfiguration config) {
		super(config);
		this.config = config;
	}

	@Override
	protected void configure () {
		super.configure();
		// TODO cleanup: cluster in methods or something like that
		bind(BifrostConfiguration.class).toInstance(this.config);

		bind(DatagramEndpoint.class).annotatedWith(KeyExchange.class).to(UDPDatagramEndpoint.class).in(Singleton.class);
		bind(DatagramEndpoint.class).annotatedWith(Payload.class).to(UDPDatagramEndpoint.class).in(Singleton.class);

		bind(KeyExchangeClient.class).to(DHKeyExchangeClient.class);

		bind(CounterpartAddress.class).annotatedWith(Payload.class)
				.toProvider(new CounterpartAddressProvider(config.getServer().getServerHostName(), config.getServer().getServerPayloadPort()));
		bind(CounterpartAddress.class).annotatedWith(KeyExchange.class)
				.toProvider(new CounterpartAddressProvider(config.getServer().getServerHostName(), config.getServer().getServerKeyExchangePort()));
		install(new FactoryModuleBuilder()
				.implement(ClientMultiplexedDatagramEndpoint.class, ClientMultiplexedDatagramEndpoint.class)
				.build(ClientMultiplexedDatagramEndpointFactory.class));

		switch (config.getSessionConverter().getType()) {
			case DEFAULT:
				install(new FactoryModuleBuilder()
						.implement(SessionConverter.class, NoCryptoSessionConverter.class)
						.build(DefaultClientSessionConverterFactory.class));
				bind(SessionConverterFactory.class).to(DefaultClientSessionConverterFactory.class);
				break;
			case NO_CRYPTO:
				install(new FactoryModuleBuilder()
						.implement(SessionConverter.class, NoCryptoSessionConverter.class)
						.build(NoCryptoSessionConverterFactory.class));
				bind(SessionConverterFactory.class).to(NoCryptoSessionConverterFactory.class);
				break;
			default:
				// throw some error or something
				break;
		}

	}

}
