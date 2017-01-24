package ch.bifrost.core.api.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BifrostConfiguration {

	private KeyExchangeConfiguration keyExchange = new KeyExchangeConfiguration();
	private SessionConverterConfiguration sessionConverter = new SessionConverterConfiguration();
	private ServerConfiguration server = new ServerConfiguration();

	public void writeToFile (String pathname) throws JsonGenerationException, JsonMappingException, IOException {
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(pathname), this);
	}

	public static BifrostConfiguration readFromFile (String pathname) throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(new File(pathname), BifrostConfiguration.class);
	}

}
