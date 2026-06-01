package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BoutTest {
    @Test
    fun `attack() adds card to attackDeck`() {
        val bout = Bout()
        bout.attack(Card(Suit.SPADES, Rank.QUEEN))
        assertThat(bout.attackDeck.contains(Card(Suit.SPADES, Rank.QUEEN))).isTrue()
    }
    @Test
    fun `defense() adds card to defenseDeck`() {
        val bout = Bout()
        bout.defense(Card(Suit.SPADES, Rank.QUEEN))
        assertThat(bout.defenseDeck.contains(Card(Suit.SPADES, Rank.QUEEN))).isTrue()
    }
}