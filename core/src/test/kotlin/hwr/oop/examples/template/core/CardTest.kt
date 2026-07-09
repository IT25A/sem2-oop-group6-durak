package hwr.oop.examples.template.core

import hwr.oop.examples.template.core.CardFromStringConverter.asCard
import org.assertj.core.api.Assertions.assertThat
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

    @ParameterizedTest
    @EnumSource(Rank::class)
    fun `toShortString rank part is correct`(rank: Rank) {
        val card = Card(Suit.CLUBS, rank)
        val expected = when (rank) {
            Rank.SIX   -> "6C"
            Rank.SEVEN -> "7C"
            Rank.EIGHT -> "8C"
            Rank.NINE  -> "9C"
            Rank.TEN   -> "10C"
            Rank.JACK  -> "JC"
            Rank.QUEEN -> "QC"
            Rank.KING  -> "KC"
            Rank.ACE   -> "AC"
        }
        assertThat(card.toShortString()).isEqualTo(expected)
    }

    @ParameterizedTest
    @EnumSource(Suit::class)
    fun `toShortString suit part is correct`(suit: Suit) {
        val card = Card(suit, Rank.ACE)
        val expected = when (suit) {
            Suit.CLUBS    -> "AC"
            Suit.DIAMONDS -> "AD"
            Suit.HEARTS   -> "AH"
            Suit.SPADES   -> "AS"
        }
        assertThat(card.toShortString()).isEqualTo(expected)
    }

    @ParameterizedTest
    @EnumSource(Rank::class)
    fun `toShortString round-trips through CardFromStringConverter`(rank: Rank) {
        Suit.entries.forEach { suit ->
            val card = Card(suit, rank)
            assertThat(card.toShortString().asCard()).isEqualTo(card)
        }
    }
}