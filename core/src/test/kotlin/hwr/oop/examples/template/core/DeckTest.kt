package hwr.oop.examples.template.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeckTest {
	val deck = Deck()

	@Test
	fun `deck has 36 cards after init`() {
		assertEquals(36, deck.remaining())
	}
	@Test
	fun `deck contains 9 of each suit`() {
		val groupedBySuit = deck.getCards().groupBy { it.suit }
		groupedBySuit.forEach { (_, suitCards) ->
			assertEquals(9, suitCards.size)
		}
	}
	@Test
	fun `deck contains 4 of each rank`() {
		val groupedByRank = deck.getCards().groupBy { it.rank }
		groupedByRank.forEach { (_, rankCards) ->
			assertEquals(4, rankCards.size)
		}
	}
	@Test
	fun `last card of deck is trump`() {
		val expectedCard = deck.getCards().last()
		assertEquals(deck.peekTrump(), expectedCard)
	}
	@Test
	fun `clearDeckForTest should empty the deck`() {
		assertTrue(deck.remaining() > 0)
		deck.clearDeckForTest()
		assertTrue(deck.getCards().isEmpty())
	}
}