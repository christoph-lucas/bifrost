package ch.bifrost.core.api.config;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;

public class BifrostConfigurationTest {

	private static final String FILE_SUFFIX = "json";
	private static final String FILE_PREFIX = "config.temp";

	@Test
	public void fileOperationsWorkAsExpected () throws Exception {
		String pathname = Files.createTempFile(FILE_PREFIX, FILE_SUFFIX).toString();
		try {
			BifrostConfiguration origConfig = getConfig();

			origConfig.writeToFile(pathname);
			BifrostConfiguration readFromFile = BifrostConfiguration.readFromFile(pathname);

			assertThat(readFromFile, is(equalTo(origConfig)));
		} finally {
			File configFile = new File(pathname);
			if (configFile.exists()) {
				configFile.delete();
			}
		}
	}

	private BifrostConfiguration getConfig () {
		BifrostConfiguration config = new BifrostConfiguration();

		DHKeyExchangeAlgorithmConfiguration dhAlgo = new DHKeyExchangeAlgorithmConfiguration().setDhParams("RFC3526_4096");
		config.getKeyExchange().setAlgorithm(dhAlgo);

		return config;
	}

}
