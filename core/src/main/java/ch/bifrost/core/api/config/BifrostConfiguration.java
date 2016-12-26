package ch.bifrost.core.api.config;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class BifrostConfiguration {

	private KeyExchangeConfiguration keyExchange = new KeyExchangeConfiguration();
	private SessionConverterConfiguration sessionConverter = new SessionConverterConfiguration();
	private ServerConfiguration server = new ServerConfiguration();
	
}
