package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlayerTest {
    @Test
    fun `player can draw`(){
        val deck = Deck()
        val player = Player()
        val topCard = deck.getCards().firstOrNull()
        assertThat(player.draw(deck)).isTrue()
        assertThat(player.hand.contains(topCard)).isTrue()
        assertThat(deck.getCards().contains(topCard)).isFalse()
    }
    @Test
    fun `drawing from an empty deck returns false`(){
        val deck = Deck()
        val player = Player()
        deck.getCards().forEach { _ ->
            player.draw(deck)
        }
        val exception = assertThrows(IllegalArgumentException::class.java) {
            player.draw(deck)
        }
        assertTrue(exception.message!!.contains("Drawn card not found"))
    }
}