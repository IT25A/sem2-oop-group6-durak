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
    fun `determineNextTurn with defender as passingPlayer skips losing defender`(){
        game.determineNextTurn(game.currentDefender())
        assertEquals(game.players[2], game.currentAttacker())
        assertEquals(game.players[0], game.currentDefender())
    }
    @Test
    fun `refillHands succeeds`() {
        game.players.forEach { it.hand.clear() }
        repeat(5) { game.players[0].hand.add(Card(Suit.CLUBS, Rank.QUEEN)) }
        repeat(4) { game.players[1].hand.add(Card(Suit.CLUBS, Rank.QUEEN)) }
        repeat(3) { game.players[2].hand.add(Card(Suit.CLUBS, Rank.QUEEN)) }

        game.deck.clearDeckForTest()
        repeat(4) { game.deck.addCardToDeckForTest(Card(Suit.CLUBS, Rank.ACE)) }

        game.refillHands()

        assert(game.players[0].hand.size == 6)
        assert(game.players[1].hand.size == 6)
        assert(game.players[2].hand.size == 4)
    }
    @Test
    fun `handlePlayerFinished adds player to win order`() {
        player.hand.clear()
        game.deck.clearDeckForTest()

        game.handlePlayerFinished(player, false)
        assertEquals(game.playerWinOrder.size, 1)
    }
    @Test
    fun `handlePlayerFinished if defending player`() {
        player.hand.clear()
        game.deck.clearDeckForTest()

        game.handlePlayerFinished(player, true)
        assertEquals(game.playerWinOrder.size, 1)
    }
    @Test
    fun `determineGameOver changes gamePhase to FINISHED`(){
        game.playerWinOrder.add(game.players[0])
        game.playerWinOrder.add(game.players[1])
        game.determineGameOver()
        assertEquals(GamePhase.FINISHED, game.getGamePhaseForTest())
    }
}