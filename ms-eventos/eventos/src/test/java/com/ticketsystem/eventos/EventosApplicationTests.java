package com.ticketsystem.eventos;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

<<<<<<< HEAD
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

=======
@Disabled("Requiere base de datos MySQL en ejecucion")
@SpringBootTest
>>>>>>> 149f6c408149174db7461989f988bcae9ec98e3e
class EventosApplicationTests {

	@Test
	void applicationClassIsPresent() {
		assertDoesNotThrow(() -> Class.forName("com.ticketsystem.eventos.EventosApplication"));
	}

}
