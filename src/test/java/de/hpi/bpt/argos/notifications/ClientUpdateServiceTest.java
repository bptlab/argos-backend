package de.hpi.bpt.argos.notifications;

import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.testUtil.WebSocket;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ClientUpdateServiceTest extends ArgosTestParent {

	@Test
	public void testConnection() {
		WebSocket webSocket = new WebSocket();

		assertEquals(true, webSocket.connectToServer());
	}
}
