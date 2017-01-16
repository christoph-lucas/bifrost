package ch.bifrost.core.api.config;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SessionConverterConfiguration {

	private SessionConverterType type = SessionConverterType.DEFAULT;

	public static enum SessionConverterType {
		DEFAULT, NO_CRYPTO;
	}
}
