package ch.bifrost.core.api.config;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class KeyExchangeConfiguration {

	private KeyExchangeAlgorithmConfiguration algorithm;

}
