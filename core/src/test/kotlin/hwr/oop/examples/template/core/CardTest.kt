package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class CardTest {
    @Test
    fun `all ranks exist`() {
        val ranks = Rank.entries
        assertThat(ranks).containsExactlyInAnyOrder(
            Rank.SIX,
            Rank.SEVEN,
            Rank.EIGHT,
            Rank.NINE,
            Rank.TEN,
            Rank.JACK,
            Rank.QUEEN,
            Rank.KING,
            Rank.ACE,
        )
    }
    @ParameterizedTest
    @EnumSource(Suit::class)
    fun `all ranks, each suit can exist`(suit: Suit) {
        val allRanks = Rank.entries
        val cards = allRanks.map { Card(suit, it) }

        assertThat(cards)
            .hasSize(allRanks.size)
            .allMatch { it.suit == suit }

        val ranks = cards.map { it.rank }
        assertThat(ranks).containsExactlyInAnyOrderElementsOf(allRanks)
    }
    @Test
    fun `all suits exist`() {
        val suits = Suit.entries
        assertThat(suits).containsExactlyInAnyOrder(
            Suit.SPADES,
            Suit.HEARTS,
            Suit.DIAMONDS,
            Suit.CLUBS
        )
    }
    @ParameterizedTest
    @EnumSource(Rank::class)
    fun `all suits, each rank can exist`(rank: Rank) {
        val allSuits = Suit.entries
        val cards = allSuits.map { Card(it, rank) }

        assertThat(cards)
            .hasSize(allSuits.size)
            .allMatch { it.rank == rank }

        val suits = cards.map { it.suit }
        assertThat(suits).containsExactlyInAnyOrderElementsOf(allSuits)
    }
    @Test
    fun `toString() returns suit and rank`() {
        val card = Card(Suit.SPADES, Rank.QUEEN)
        val result = card.toString()
        assertEquals("QS", result)
    }
}