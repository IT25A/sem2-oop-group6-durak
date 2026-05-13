package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameTest {
    @Test
    fun `player draws 6 cards`(){
        val game = Game()
        val player = game.attackingPlayer
        val playerHand = player.hand
        assertThat(playerHand).hasSize(6)
    }
}