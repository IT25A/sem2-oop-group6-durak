package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeckTest {
	@Test
	fun `deck has 36 cards after init`() {
		val deck = Deck()
		assertThat { 36 == deck.remaining() }
	}
}