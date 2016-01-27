package ch.bifrost.core.api.datagram;

import java.net.DatagramSocket;

public interface DatagramPacketHandlerFactory {

	DatagramPacketHandler getHandlerFor(IncomingPacket packet, DatagramSocket socket);

}