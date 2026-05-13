package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class PlayerTest {
    @Test
    fun `player can draw`(){
        val deck = Deck()
        val player = Player()
        val topCard = deck.getCards().firstOrNull()
        player.draw(deck)
        assertThat(player.hand.contains(topCard)).isTrue()
        assertThat(deck.getCards().contains(topCard)).isFalse()
    }

}