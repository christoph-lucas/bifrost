package ch.bifrost.server.impl.session;

import java.util.concurrent.BlockingQueue;

import ch.bifrost.core.api.session.SessionPacket;
import ch.bifrost.server.api.server.ServerProcess;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represent the state of a session.
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SessionState {

	private String id;
	private BlockingQueue<SessionPacket> receivedPackages;
	private ServerProcess serverProcess;
	
}