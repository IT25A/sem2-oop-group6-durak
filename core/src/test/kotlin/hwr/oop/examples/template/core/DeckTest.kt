package hwr.oop.examples.template.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeckTest {
	@Test
	fun `deck has 36 cards after init`() {
		val deck = Deck()
		assertEquals(36, deck.remaining())
	}
	@Test
	fun `deck contains 9 of each suit`() {
		val deck = Deck()
		val groupedBySuit = deck.getCards().groupBy { it.suit }
		groupedBySuit.forEach { (suit, suitCards) ->
			assertEquals(9, suitCards.size)
		}
	}
	@Test
	fun `deck contains 4 of each rank`() {
		val deck = Deck()
		val groupedByRank = deck.getCards().groupBy { it.rank }
		groupedByRank.forEach { (rank, rankCards) ->
			assertEquals(4, rankCards.size)
		}
	}
	@Test
	fun `last card of deck is trump`() {
		val deck = Deck()
		val expectedCard = deck.getCards().last()
		assertEquals(deck.peekTrump(), expectedCard)
	}
}