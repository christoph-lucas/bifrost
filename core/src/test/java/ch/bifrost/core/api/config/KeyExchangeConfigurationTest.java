package ch.bifrost.core.api.config;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.hamcrest.Matchers;
import org.junit.Test;

public class KeyExchangeConfigurationTest {

	@Test
	public void algorithmHasCorrectType () throws Exception {
		BifrostConfiguration config = getConfig("RFC3526_4096");
		KeyExchangeAlgorithmConfiguration algorithm = config.getKeyExchange().getAlgorithm();
		assertThat(algorithm, is(Matchers.instanceOf(DHKeyExchangeAlgorithmConfiguration.class)));
	}

	@Test
	public void parsesDhKeyExchangeAlgorithmParamsWith4096ConfigurationCorrectly () throws Exception {
		BifrostConfiguration config = getConfig("RFC3526_4096");
		DHKeyExchangeAlgorithmConfiguration dhAlgo = (DHKeyExchangeAlgorithmConfiguration) config.getKeyExchange().getAlgorithm();
		assertThat(dhAlgo.getParsedDHParams(), is(equalTo(DHStandardGroups.rfc3526_4096)));
	}

	@Test
	public void parsesDhKeyExchangeAlgorithmParamsWith2048ConfigurationCorrectly () throws Exception {
		BifrostConfiguration config = getConfig("RFC3526_2048");
		DHKeyExchangeAlgorithmConfiguration dhAlgo = (DHKeyExchangeAlgorithmConfiguration) config.getKeyExchange().getAlgorithm();
		assertThat(dhAlgo.getParsedDHParams(), is(equalTo(DHStandardGroups.rfc3526_2048)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsToParseNonExistingDhParams () throws Exception {
		BifrostConfiguration config = getConfig("RFC3526_1024");
		DHKeyExchangeAlgorithmConfiguration dhAlgo = (DHKeyExchangeAlgorithmConfiguration) config.getKeyExchange().getAlgorithm();
		dhAlgo.getParsedDHParams();
	}

	protected BifrostConfiguration getConfig (String dhParams) {
		BifrostConfiguration config = new BifrostConfiguration();
		DHKeyExchangeAlgorithmConfiguration dhAlgo = new DHKeyExchangeAlgorithmConfiguration().setDhParams(dhParams);
		config.getKeyExchange().setAlgorithm(dhAlgo);
		return config;
	}

}
