package ch.bifrost.core.api.config;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DHKeyExchangeAlgorithmConfiguration extends KeyExchangeAlgorithmConfiguration {

	public static final String TYPE_NAME = "dh";

	private String dhParams = "RFC3526_2048";

	public DHKeyExchangeAlgorithmConfiguration () {
		super(TYPE_NAME);
	}

	@JsonIgnore
	public DHParameters getParsedDHParams () {
		return DhParams.valueOf(this.dhParams).getParams();
	}

	public static enum DhParams {

		RFC3526_2048(DHStandardGroups.rfc3526_2048),
		RFC3526_4096(DHStandardGroups.rfc3526_4096);

		@Getter
		private final DHParameters params;

		private DhParams (DHParameters params) {
			this.params = params;
		}

	}

}
