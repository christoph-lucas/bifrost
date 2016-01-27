package ch.bifrost.client.test;

public class UDPTestClient {

	public static void main(String[] args) throws Exception {
		DatagramClientWithReply client = new DatagramClientWithReply("localhost", 34543);
		String reply = client.send("Hello World!");
		System.out.println("Client: " + reply);
	}
	
}
