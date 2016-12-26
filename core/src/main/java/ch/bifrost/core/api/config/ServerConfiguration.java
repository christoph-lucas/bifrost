package ch.bifrost.core.api.config;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class ServerConfiguration {

	private String serverHostName = "localhost";
	private int serverKeyExchangePort = 12345;
	private int serverPayloadPort = 12346;
	
}
