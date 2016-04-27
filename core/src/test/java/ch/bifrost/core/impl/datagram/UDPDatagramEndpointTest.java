package ch.bifrost.core.impl.datagram;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.impl.MessageCodecUtils;

public class UDPDatagramEndpointTest {
	
	private final String SERVER_HOST = "localhost";
	private final int SERVER_PORT = 34543;
	private final byte[] CLIENT_MESSAGE = MessageCodecUtils.encodeStringAsByteArray("Hello World!");
	private final byte[] SERVER_MESSAGE = MessageCodecUtils.encodeStringAsByteArray("Hello Client!");
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
		CounterpartAddress serverAddress = new CounterpartAddress(SERVER_HOST, SERVER_PORT);
		DatagramMessage outgoing = new DatagramMessage(serverAddress, CLIENT_MESSAGE);

		clientEndpoint.send(outgoing);
		DatagramMessage incoming = serverEndpoint.receive();

		assertThat(incoming.getPayload(), is(equalTo(CLIENT_MESSAGE)));
	}
	
	@Test
	public void shouldBeAbleToReply() throws Exception {
		CounterpartAddress serverAddress = new CounterpartAddress(SERVER_HOST, SERVER_PORT);
		DatagramMessage message = new DatagramMessage(serverAddress, CLIENT_MESSAGE);
		clientEndpoint.send(message);

		DatagramMessage incoming = serverEndpoint.receive();
		DatagramMessage reply = new DatagramMessage(incoming.getCounterpartAddress(), SERVER_MESSAGE);
		serverEndpoint.send(reply);
		
		DatagramMessage incomingReply = clientEndpoint.receive();
		assertThat(incomingReply.getPayload(), is(equalTo(SERVER_MESSAGE)));
	}

}
