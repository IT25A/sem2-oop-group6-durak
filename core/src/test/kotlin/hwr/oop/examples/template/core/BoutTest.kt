package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BoutTest {
    val bout = Bout()

    @Test
    fun `attack() adds card to attackDeck`() {
        bout.attack(Card(Suit.SPADES, Rank.QUEEN))
        assertThat(bout.attackDeck.contains(Card(Suit.SPADES, Rank.QUEEN))).isTrue()
    }
    @Test
    fun `defense() adds card to defenseDeck`() {
        bout.defense(Card(Suit.SPADES, Rank.QUEEN))
        assertThat(bout.defenseDeck.contains(Card(Suit.SPADES, Rank.QUEEN))).isTrue()
    }
}