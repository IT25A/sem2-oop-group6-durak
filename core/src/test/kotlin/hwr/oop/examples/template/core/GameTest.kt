package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameTest {
    val game = Game(GameId("1"), listOf("Alice", "Bob", "Charlie"))
    val player = game.players[0]

    @Test
    fun `init draws 6 cards to each player`(){
        assertThat(game.players[0].hand).hasSize(6)
        assertThat(game.players[1].hand).hasSize(6)
        assertThat(game.players[2].hand).hasSize(6)

        assertThat(game.deck.getCards()).hasSize(36 - 18)
    }
    @Test
    fun `trump uses override when provided`() {
        val game = Game(GameId("1"), listOf("Alice", "Bob", "Charlie"), Suit.HEARTS)

        assertEquals(Suit.HEARTS, game.trump)
    }
    @Test
    fun `trump is taken from deck when no override is provided`() {
        assertNotNull(game.trump)
        assertEquals(game.deck.peekTrump().suit, game.trump)
    }
    @Test
    fun `getNextNonEmptyPlayerIndex skips multiple empty hands`(){
        game.players[0].hand.clear()
        game.players[1].hand.clear()
        val nextIndex = game.getNextNonEmptyPlayerIndex(0)
        assertEquals(2, nextIndex)
    }
    @Test
    fun `determineNextTurn with attacker as passingPlayer updates both indices`(){
        val attackerBefore = game.attackingPlayer
        val defenderBefore = game.defendingPlayer

        game.determineNextTurn(attackerBefore)

        assertNotEquals(attackerBefore, game.attackingPlayer)
        assertNotEquals(defenderBefore, game.defendingPlayer)
    }

    @Test
    fun `determineNextTurn with defender as passingPlayer updates both indices`(){
        val attackerBefore = game.attackingPlayer
        val defenderBefore = game.defendingPlayer

        game.determineNextTurn(defenderBefore)

        assertNotEquals(attackerBefore, game.attackingPlayer)
        assertNotEquals(defenderBefore, game.defendingPlayer)
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
    fun `determineNextTurn with attacker as passingPlayer skips losing attacker`(){
        game.determineNextTurn(game.attackingPlayer)
        assertEquals(game.players[1], game.attackingPlayer)
        assertEquals(game.players[2], game.defendingPlayer)
    }
    @Test
    fun `determineNextTurn with defender as passingPlayer skips losing defender`(){
        game.determineNextTurn(game.defendingPlayer)
        assertEquals(game.players[2], game.attackingPlayer)
        assertEquals(game.players[0], game.defendingPlayer)
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
    @Test
    fun `determineGameOver finishes when 1 player left`() {
        for (i in 0..<game.players.size-1){
            game.playerWinOrder.add(game.players[i])
        }
        game.determineGameOver()
        assertThat(game.players.size - game.playerWinOrder.size == 1)
        assertEquals(GamePhase.FINISHED, game.getGamePhaseForTest())
    }
    @Test
    fun `determineGameOver finishes when 0 players left`() {
        for (i in game.players.indices){
            game.playerWinOrder.add(game.players[i])
        }
        game.determineGameOver()
        assertThat(game.players.size - game.playerWinOrder.size == 0)
        assertEquals(GamePhase.FINISHED, game.getGamePhaseForTest())
    }
    @Test
    fun `determineGameOver doesn't finish when 2 players left`() {
        for (i in 0..<game.players.size-2){
            game.playerWinOrder.add(game.players[i])
        }
        game.determineGameOver()
        assertThat(game.players.size - game.playerWinOrder.size == 2)
        assertNotEquals(GamePhase.FINISHED, game.getGamePhaseForTest())
    }
}