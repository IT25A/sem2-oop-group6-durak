package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameTest {
    @Test
    fun `player draws 6 cards`(){
        val game = Game(1,listOf("Bob", "Alice"))
        val player = game.players[0]
        val playerHand = player.hand
        assertThat(playerHand).hasSize(6)
    }
}