package ch.bifrost.core.api.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdKeyPair {

	private final String id;
	private final byte[] key;
	
	public static IdKeyPair decode(String content) throws Exception {
		return new ObjectMapper().readValue(content, IdKeyPair.class);
	}
	
	public String encode() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
	
}
