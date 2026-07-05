package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerTest {
    private lateinit var deck: Deck
    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        deck = Deck.createShuffled()
        player = Player.create("")
    }

    @Test
    fun `player name is set correctly`() {
        val player = Player.create("Alice")
        assertThat(player.name).isEqualTo("Alice")
    }
    @Test
    fun `player can draw`(){
        val topCard = deck.getCards().firstOrNull()
        player.draw(deck)
        assertThat(player.getHand().contains(topCard)).isTrue()
        assertThat(deck.getCards().contains(topCard)).isFalse()
    }
    @Test
    fun `drawing from an empty deck returns false`(){
        deck.getCards().forEach { _ ->
            player.draw(deck)
        }
        val exception = assertThrows(EmptyDeckException::class.java) {
            player.draw(deck)
        }
        assertTrue(exception.message!!.contains("Drawn card not found"))
    }
}