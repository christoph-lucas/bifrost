package ch.bifrost.core.impl.datagram;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.bifrost.core.api.datagram.Packet;

public class UDPDatagramEndpointTest {
	
	private final String SERVER_HOST = "localhost";
	private final int SERVER_PORT = 34543;
	private final String CLIENT_MESSAGE = "Hello World!";
	private final String SERVER_MESSAGE = "Hello Client!";
	private UDPDatagramEndpoint serverEndpoint;
	private UDPDatagramEndpoint clientEndpoint;

	@Before
	public void setupEndpoints() throws Exception {
		serverEndpoint = new UDPDatagramEndpoint(SERVER_PORT);
		clientEndpoint = new UDPDatagramEndpoint();
	}
	
	@After
	public void closeEndpoints() throws Exception {
		serverEndpoint.close();
		clientEndpoint.close();
	}
	
	@Test
	public void shouldReceiveWhatWasSent() throws Exception {
		Packet outgoing = new Packet(SERVER_HOST, SERVER_PORT, CLIENT_MESSAGE);

		clientEndpoint.send(outgoing);
		Packet incoming = serverEndpoint.receive();

		assertThat(incoming.getContent(), is(equalTo(CLIENT_MESSAGE)));
	}
	
	@Test
	public void shouldBeAbleToReply() throws Exception {
		Packet message = new Packet(SERVER_HOST, SERVER_PORT, CLIENT_MESSAGE);
		clientEndpoint.send(message);

		Packet incoming = serverEndpoint.receive();
		Packet reply = new Packet(incoming.getCounterpartAddress(), incoming.getCounterpartPort(), SERVER_MESSAGE);
		serverEndpoint.send(reply);
		
		Packet incomingReply = clientEndpoint.receive();
		assertThat(incomingReply.getContent(), is(equalTo(SERVER_MESSAGE)));
	}

}
