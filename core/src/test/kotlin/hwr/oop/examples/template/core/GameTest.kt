package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameTest {
    val game = Game(1, listOf("Alice", "Bob", "Charlie"))
    val player = game.players[0]

    @Test
    fun `player draws 6 cards`(){
        val playerHand = player.hand
        assertThat(playerHand).hasSize(6)
    }
    @Test
    fun `getNextNonEmptyPlayerIndex skips multiple empty hands`(){
        game.players[0].hand.clear()
        game.players[1].hand.clear()
        val nextIndex = game.getNextNonEmptyPlayerIndex(0)
        assertEquals(2, nextIndex)
    }
    @Test
    fun `determineNextTurn should update attacker and defender after attacker passes`(){
        val initialAttacker = game.currentAttacker()
        game.determineNextTurn(initialAttacker)
        assertEquals(game.players[1], game.currentAttacker())
        assertEquals(game.players[2], game.currentDefender())
    }
    @Test
    fun `test determineNextTurn throws on invalid player`(){
        val unknownPlayer = Player("Unknown")
        val exception = assertThrows(IllegalArgumentException::class.java){
            game.determineNextTurn(unknownPlayer)
        }
        assertTrue(exception.message!!.contains("invalid"))
    }
    @Test
    fun `determineNextTurn with defender as passingPlayer updates only defender`(){
        val attackerBefore = game.currentAttacker()
        val defenderBefore = game.currentDefender()
        game.determineNextTurn(defenderBefore)
        assertEquals(attackerBefore, game.currentAttacker())
        assertEquals(defenderBefore, game.currentDefender())
    }
}