package com.ticket_system.artistas;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Disabled("Requiere base de datos MySQL en ejecucion")
@SpringBootTest
class ArtistasApplicationTests {

	@Test
	void applicationClassIsPresent() {
		assertDoesNotThrow(() -> Class.forName("com.ticket_system.ArtistasApplication"));
	}

}
