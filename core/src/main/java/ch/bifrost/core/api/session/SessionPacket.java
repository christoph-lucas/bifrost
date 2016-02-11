package ch.bifrost.core.api.session;

import java.io.IOException;
import java.net.InetAddress;

import ch.bifrost.core.api.datagram.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A packet inside the session layer, not used outside the session layer.
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class SessionPacket {

	private InetAddress counterpartAddress;
	private int counterpartPort;
	private String sessionId;
	private String content;

	public static SessionPacket fromPacket(Packet packet) {
		String rawContent = packet.getContent();
		ObjectMapper mapper = new ObjectMapper();
		Content value;
		try {
			value = mapper.readValue(rawContent, Content.class);
		} catch (IOException e) {
			throw new SessionException("Cannot decode Packet from Json.", e);
		}
		String sessionId = value.getSessionId();
		String content = value.getContent();
		
		return new SessionPacket(packet.getCounterpartAddress(), packet.getCounterpartPort(), sessionId, content);
	}
	
	public Packet toPacket() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String encodedContent = mapper.writeValueAsString(new Content(sessionId, content));
			return new Packet(counterpartAddress, counterpartPort, encodedContent);
		} catch (JsonProcessingException e) {
			throw new SessionException("Cannot encode Packet to Json.", e);
		}
	}

	@Data
	@AllArgsConstructor
	private static class Content {
		private String sessionId;
		private String content;
	}
	
}