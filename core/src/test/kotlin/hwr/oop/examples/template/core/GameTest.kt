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
    private lateinit var bout: Bout

    @BeforeEach
    fun setUp() {
        game = Game.createRandomGame(
            playerNames = listOf("Alice", "Bob", "Charlie"),
            gameId = GameId("1")
        )
        player = game.players[0]
        bout = game.bout
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
        assertThat(game.players[0].getHand()).hasSize(6)
        assertThat(game.players[1].getHand()).hasSize(6)
        assertThat(game.players[2].getHand()).hasSize(6)

        assertThat(game.deck.getCards()).hasSize(36 - 18)
    }
    @Test
    fun `trump is taken from the last card in the deck`() {
        val deck = Deck.createShuffled()
        deck.clearDeck()
        repeat(18) { deck.addCardToDeck(Card(Suit.HEARTS, Rank.SIX)) }
        deck.addCardToDeck(Card(Suit.CLUBS, Rank.ACE))
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
    fun `player with lowest trump card is first attacker`() {
        val deck = Deck.createShuffled()
        deck.clearDeck()
        listOf(
            Card(Suit.CLUBS, Rank.SIX),
            Card(Suit.CLUBS, Rank.SEVEN),
            Card(Suit.CLUBS, Rank.EIGHT),
            Card(Suit.CLUBS, Rank.NINE),
            Card(Suit.CLUBS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),   // Alice's lowest trump
            Card(Suit.SPADES, Rank.SIX),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.EIGHT),
            Card(Suit.SPADES, Rank.NINE),
            Card(Suit.SPADES, Rank.TEN),
            Card(Suit.HEARTS, Rank.ACE),   // Bob's higher trump
            Card(Suit.HEARTS, Rank.KING),  // trump card at bottom
        ).forEach { deck.addCardToDeck(it) }
        val createdGame = Game.createGameFromDeck(playerNames = listOf("Alice", "Bob"), deck = deck)
        assertEquals("Alice", createdGame.attackingPlayer.name)
        assertEquals("Bob", createdGame.defendingPlayer.name)
    }
    @Test
    fun `player with no trump card does not become first attacker when another has trump`() {
        val deck = Deck.createShuffled()
        deck.clearDeck()
        listOf(
            Card(Suit.CLUBS, Rank.SIX),
            Card(Suit.CLUBS, Rank.SEVEN),
            Card(Suit.CLUBS, Rank.EIGHT),
            Card(Suit.CLUBS, Rank.NINE),
            Card(Suit.CLUBS, Rank.TEN),
            Card(Suit.CLUBS, Rank.JACK),   // Alice: no trump
            Card(Suit.SPADES, Rank.SIX),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.EIGHT),
            Card(Suit.SPADES, Rank.NINE),
            Card(Suit.SPADES, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),   // Bob's only trump
            Card(Suit.HEARTS, Rank.KING),  // trump card at bottom
        ).forEach { deck.addCardToDeck(it) }
        val createdGame = Game.createGameFromDeck(playerNames = listOf("Alice", "Bob"), deck = deck)
        assertEquals("Bob", createdGame.attackingPlayer.name)
        assertEquals("Alice", createdGame.defendingPlayer.name)
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
        val exception = assertThrows(InvalidPlayerTurnException::class.java){
            game.determineNextTurn(unknownPlayer)
        }
        assertTrue(exception.message!!.contains("invalid"))
    }
    @Test
    fun `determineNextTurn with attacker as passingPlayer skips losing attacker`(){
        val defenderBefore = game.defendingPlayer
        val defenderIndex = game.players.indexOf(defenderBefore)
        game.determineNextTurn(game.attackingPlayer)
        assertEquals(defenderBefore, game.attackingPlayer)
        assertEquals(game.players[(defenderIndex + 1) % game.players.size], game.defendingPlayer)
    }
    @Test
    fun `determineNextTurn with defender as passingPlayer skips losing defender`(){
        val defenderIndex = game.players.indexOf(game.defendingPlayer)
        val expectedNextAttacker = game.players[(defenderIndex + 1) % game.players.size]
        game.determineNextTurn(game.defendingPlayer)
        assertEquals(expectedNextAttacker, game.attackingPlayer)
        assertEquals(game.players[(game.players.indexOf(expectedNextAttacker) + 1) % game.players.size], game.defendingPlayer)
    }
    @Test
    fun `refillHands succeeds`() {
        val ai = game.players.indexOf(game.attackingPlayer)
        val p0 = game.players[ai % 3]
        val p1 = game.players[(ai + 1) % 3]
        val p2 = game.players[(ai + 2) % 3]
        game.players.forEach { it.clearHand() }
        repeat(5) { p0.addToHand(Card(Suit.CLUBS, Rank.QUEEN)) }
        repeat(4) { p1.addToHand(Card(Suit.CLUBS, Rank.QUEEN)) }
        repeat(3) { p2.addToHand(Card(Suit.CLUBS, Rank.QUEEN)) }

        game.deck.clearDeck()
        repeat(4) { game.deck.addCardToDeck(Card(Suit.CLUBS, Rank.ACE)) }

        game.refillHands()

        assertThat(p0.getHand()).hasSize(6)
        assertThat(p1.getHand()).hasSize(6)
        assertThat(p2.getHand()).hasSize(4)
    }
    @Test
    fun `handlePlayerFinished adds player to win order`() {
        player.clearHand()
        game.deck.clearDeck()

        game.handlePlayerFinished(player, false)
        assertEquals(game.getPlayerWinOrder().size, 1)
    }
    @Test
    fun `handlePlayerFinished sets game to FINISHED when only one player remains`() {
        game.deck.clearDeck()
        game.players[0].clearHand()
        game.players[1].clearHand()
        game.getPlayerWinOrder().add(game.players[0])

        game.handlePlayerFinished(game.players[1], false)

        assertEquals(GamePhase.FINISHED, game.getGamePhase())
    }
    @Test
    fun `handlePlayerFinished if defending player`() {
        player.clearHand()
        game.deck.clearDeck()

        game.handlePlayerFinished(player, true)
        assertEquals(game.getPlayerWinOrder().size, 1)
    }
    @Test
    fun `handlePlayerFinished calls pass when defender finishes with undefended attack`() {
        val defender = game.defendingPlayer
        game.setGamePhase(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.SIX))
        defender.clearHand()
        game.deck.clearDeck()

        game.handlePlayerFinished(defender, true)

        assertEquals(1, game.getPlayerWinOrder().size)
        assertTrue(game.bout.getAttackDeck().isEmpty())
    }
    @Test
    fun `finishBout with defenderSucceeded false clears bout after giving cards to defender`() {
        val attackCard = Card(Suit.CLUBS, Rank.SIX)
        val defenseCard = Card(Suit.HEARTS, Rank.SEVEN)
        bout.addAttackCard(attackCard)
        bout.addDefenseCard(defenseCard)
        val defender = game.defendingPlayer
        val handSizeBefore = defender.getHand().size

        game.finishBout(false)

        assertThat(defender.getHand()).hasSize(handSizeBefore + 2)
        assertThat(bout.getAttackDeck()).isEmpty()
        assertThat(bout.getDefenseDeck()).isEmpty()
    }

    @Test
    fun `determineGameOver changes gamePhase to FINISHED`(){
        game.getPlayerWinOrder().add(game.players[0])
        game.getPlayerWinOrder().add(game.players[1])
        game.determineGameOver()
        assertEquals(GamePhase.FINISHED, game.getGamePhase())
    }
    @Test
    fun `determineGameOver finishes when 1 player left`() {
        for (i in 0..<game.players.size-1){
            game.getPlayerWinOrder().add(game.players[i])
        }
        game.determineGameOver()
        assertEquals((game.players.size - game.getPlayerWinOrder().size), 1)
        assertEquals(GamePhase.FINISHED, game.getGamePhase())
    }
    @Test
    fun `determineGameOver finishes when 0 players left`() {
        for (i in game.players.indices){
            game.getPlayerWinOrder().add(game.players[i])
        }
        game.determineGameOver()
        assertEquals((game.players.size - game.getPlayerWinOrder().size), 0)
        assertEquals(GamePhase.FINISHED, game.getGamePhase())
    }
    @Test
    fun `determineGameOver doesn't finish when 2 players left`() {
        for (i in 0..<game.players.size-2){
            game.getPlayerWinOrder().add(game.players[i])
        }
        game.determineGameOver()
        assertEquals((game.players.size - game.getPlayerWinOrder().size), 2)
        assertNotEquals(GamePhase.FINISHED, game.getGamePhase())
    }
    @Test
    fun `games with different attackerIndex are not equal`() {
        val deck = Deck.createShuffled()
        val game1 = Game.createGameFromDeck(playerNames = listOf("Alice", "Bob", "Charlie"), deck = deck)
        val deck2 = Deck.createShuffled()
        val game2 = Game.createGameFromDeck(playerNames = listOf("Alice", "Bob", "Charlie"), deck = deck2)
        game2.determineNextTurn(game2.attackingPlayer)
        assertNotEquals(game1, game2)
    }
    @Test
    fun `game is not equal to null`() {
        assertNotEquals(game, null)
    }
    @Test
    fun `game is equal to itself`() {
        assertEquals(game, game)
    }
    @Test
    fun `player with strictly lower trump rank is chosen over player with adjacent higher rank`() {
        val deck = Deck.createShuffled()
        deck.clearDeck()
        listOf(
            Card(Suit.CLUBS, Rank.SIX),
            Card(Suit.CLUBS, Rank.SEVEN),
            Card(Suit.CLUBS, Rank.EIGHT),
            Card(Suit.CLUBS, Rank.NINE),
            Card(Suit.CLUBS, Rank.TEN),
            Card(Suit.HEARTS, Rank.SEVEN),  // Alice: trump rank 7
            Card(Suit.SPADES, Rank.SIX),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.SPADES, Rank.EIGHT),
            Card(Suit.SPADES, Rank.NINE),
            Card(Suit.SPADES, Rank.TEN),
            Card(Suit.HEARTS, Rank.SIX),    // Bob: trump rank 6 (strictly lower)
            Card(Suit.HEARTS, Rank.KING),
        ).forEach { deck.addCardToDeck(it) }
        val createdGame = Game.createGameFromDeck(playerNames = listOf("Alice", "Bob"), deck = deck)
        assertEquals("Bob", createdGame.attackingPlayer.name)
        assertEquals("Alice", createdGame.defendingPlayer.name)
    }
}