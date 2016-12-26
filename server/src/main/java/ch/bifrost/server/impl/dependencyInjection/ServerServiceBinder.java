package ch.bifrost.server.impl.dependencyInjection;

import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ch.bifrost.core.api.config.BifrostConfiguration;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.session.SessionConverter;
import ch.bifrost.core.api.session.SessionConverterFactory;
import ch.bifrost.core.impl.dependencyInjection.KeyExchange;
import ch.bifrost.core.impl.dependencyInjection.Payload;
import ch.bifrost.core.impl.dependencyInjection.ServiceBinder;
import ch.bifrost.core.impl.dependencyInjection.UdpDatagramEndpointProvider;
import ch.bifrost.core.impl.session.NoCryptoSessionConverter;
import ch.bifrost.core.impl.session.NoCryptoSessionConverter.NoCryptoSessionConverterFactory;
import ch.bifrost.server.api.server.ServerProcessFactory;
import ch.bifrost.server.impl.server.EchoServer.EchoServerFactory;
import ch.bifrost.server.impl.session.DefaultServerSessionConverter.DefaultServerSessionConverterFactory;

public class ServerServiceBinder extends ServiceBinder {

	private BifrostConfiguration config;

	public ServerServiceBinder (BifrostConfiguration config) {
		super(config);
		this.config = config;
	}

	@Override
	protected void configure () {
		super.configure();
		// TODO cleanup: cluster in methods or something like that
		bind(BifrostConfiguration.class).toInstance(this.config);

		bind(DatagramEndpoint.class).annotatedWith(KeyExchange.class)
				.toProvider(new UdpDatagramEndpointProvider(config.server().serverKeyExchangePort())).in(Singleton.class);
		bind(DatagramEndpoint.class).annotatedWith(Payload.class)
				.toProvider(new UdpDatagramEndpointProvider(config.server().serverPayloadPort())).in(Singleton.class);
		
		switch (config.sessionConverter().type()) {
		case DEFAULT:
			install(new FactoryModuleBuilder()
					.implement(SessionConverter.class, NoCryptoSessionConverter.class)
					.build(DefaultServerSessionConverterFactory.class));
			bind(SessionConverterFactory.class).to(DefaultServerSessionConverterFactory.class);
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

		// TODO there should probably be a better way than to hardcode this
		bind(ServerProcessFactory.class).to(EchoServerFactory.class);
		
	}

}
