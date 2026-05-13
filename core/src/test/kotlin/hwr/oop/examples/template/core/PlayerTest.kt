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
        assertThat(player.draw(deck)).isTrue()
        assertThat(player.hand.contains(topCard)).isTrue()
        assertThat(deck.getCards().contains(topCard)).isFalse()
    }
    @Test
    fun `drawing from an empty deck returns false`(){
        val deck = Deck()
        val player = Player()
        for (i in deck.getCards().indices){
            player.draw(deck)
        }
        assertThat(player.draw(deck)).isFalse
    }
    @Test
    fun `player can print their hand`(){
        val output = ByteArrayOutputStream()
    }
}