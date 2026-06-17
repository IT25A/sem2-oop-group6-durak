package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameTest {
    private lateinit var game: Game
    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        game = Game.createRandomGame(
            playerNames = listOf("Alice", "Bob", "Charlie"),
            gameId = GameId("1")
        )
        player = game.players[0]
    }

    @Test
    fun `gameId value returns the constructor string`() {
        val id = GameId("fixed-id-123")
        assertEquals("fixed-id-123", id.value)
    }

    @Test
    fun `gameId random() value is non-empty`() {
        val id = GameId.random()
        assertTrue(id.value.isNotEmpty())
    }
    @Test
    fun `init draws 6 cards to each player`(){
        assertThat(game.players[0].hand()).hasSize(6)
        assertThat(game.players[1].hand()).hasSize(6)
        assertThat(game.players[2].hand()).hasSize(6)

        assertThat(game.deck.getCards()).hasSize(36 - 18)
    }
    @Test
    fun `trump is taken from the last card in the deck`() {
        val deck = Deck.createShuffled()
        deck.clearDeckForTest()
        repeat(18) { deck.addCardToDeckForTest(Card(Suit.HEARTS, Rank.SIX)) }
        deck.addCardToDeckForTest(Card(Suit.CLUBS, Rank.ACE))
        game = Game.createGameFromDeck(
            playerNames = listOf("Alice", "Bob", "Charlie"),
            gameId = GameId("1"),
            deck = deck
        )
        assertEquals(Suit.CLUBS, game.trump)
    }
    @Test
    fun `createGameFromDeck uses default gameId when not provided`() {
        val deck = Deck.createShuffled()
        val createdGame = Game.createGameFromDeck(
            playerNames = listOf("Alice", "Bob"),
            deck = deck
        )
        assertNotNull(createdGame.gameId)
        assertTrue(createdGame.gameId.value.isNotEmpty())
    }
    @Test
    fun `createGameFromDeck uses deck trump`() {
        val deck = Deck.createShuffled()
        val expectedTrump = deck.peekTrump().suit
        val createdGame = Game.createGameFromDeck(
            playerNames = listOf("Alice", "Bob"),
            deck = deck
        )
        assertEquals(expectedTrump, createdGame.trump)
    }
    @Test
    fun `getNextNonEmptyPlayerIndex skips multiple empty hands`(){
        game.players[0].clearHand()
        game.players[1].clearHand()
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
        val unknownPlayer = Player.create("Unknown")
        val exception = assertThrows(InvalidPlayerTurn::class.java){
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
        game.players.forEach { it.clearHand() }
        repeat(5) { game.players[0].addToHand(Card(Suit.CLUBS, Rank.QUEEN)) }
        repeat(4) { game.players[1].addToHand(Card(Suit.CLUBS, Rank.QUEEN)) }
        repeat(3) { game.players[2].addToHand(Card(Suit.CLUBS, Rank.QUEEN)) }

        game.deck.clearDeckForTest()
        repeat(4) { game.deck.addCardToDeckForTest(Card(Suit.CLUBS, Rank.ACE)) }

        game.refillHands()

        assertThat(game.players[0].hand()).hasSize(6)
        assertThat(game.players[1].hand()).hasSize(6)
        assertThat(game.players[2].hand()).hasSize(4)
    }
    @Test
    fun `handlePlayerFinished adds player to win order`() {
        player.clearHand()
        game.deck.clearDeckForTest()

        game.handlePlayerFinished(player, false)
        assertEquals(game.playerWinOrder.size, 1)
    }
    @Test
    fun `handlePlayerFinished if defending player`() {
        player.clearHand()
        game.deck.clearDeckForTest()

        game.handlePlayerFinished(player, true)
        assertEquals(game.playerWinOrder.size, 1)
    }
    @Test
    fun `handlePlayerFinished calls pass when defender finishes with undefended attack`() {
        val defender = game.defendingPlayer
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.SIX))
        defender.clearHand()
        game.deck.clearDeckForTest()

        game.handlePlayerFinished(defender, true)

        assertEquals(1, game.playerWinOrder.size)
        assertTrue(game.bout.attackDeck().isEmpty())
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
        assertEquals((game.players.size - game.playerWinOrder.size), 1)
        assertEquals(GamePhase.FINISHED, game.getGamePhaseForTest())
    }
    @Test
    fun `determineGameOver finishes when 0 players left`() {
        for (i in game.players.indices){
            game.playerWinOrder.add(game.players[i])
        }
        game.determineGameOver()
        assertEquals((game.players.size - game.playerWinOrder.size), 0)
        assertEquals(GamePhase.FINISHED, game.getGamePhaseForTest())
    }
    @Test
    fun `determineGameOver doesn't finish when 2 players left`() {
        for (i in 0..<game.players.size-2){
            game.playerWinOrder.add(game.players[i])
        }
        game.determineGameOver()
        assertEquals((game.players.size - game.playerWinOrder.size), 2)
        assertNotEquals(GamePhase.FINISHED, game.getGamePhaseForTest())
    }
}