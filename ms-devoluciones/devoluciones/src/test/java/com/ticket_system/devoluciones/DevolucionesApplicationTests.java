package com.ticket_system.devoluciones;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DevolucionesApplicationTests {

	@Test
	void applicationClassIsPresent() {
		assertDoesNotThrow(() -> Class.forName("com.ticket_system.DevolucionesApplication"));
	}

}
