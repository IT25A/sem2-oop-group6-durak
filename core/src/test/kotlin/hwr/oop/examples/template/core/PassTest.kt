package hwr.oop.examples.template.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PassTest {
    private lateinit var game: Game

    @BeforeEach
    fun setUp() {
        game = Game.createRandomGame(
            playerNames = listOf("Alice", "Bob", "Charlie")
        )
    }

    @Test
    fun `pass next attacker is defender when defender takes cards`(){
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.clearDefenseDeck()

        game.pass()
        assertEquals(game.players[2], game.attackingPlayer)
    }
    @Test
    fun `pass next attacker is defender`(){
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.QUEEN))

        game.pass()
        assertEquals(game.players[1], game.attackingPlayer)
    }
    @Test
    fun `defenderTakesCards is true when phase is DEFENDING and bout is NOT defended`(){
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.clearDefenseDeck()

        game.pass()
        assertEquals(game.players[2], game.attackingPlayer)
        assertEquals(game.players[0], game.defendingPlayer)
    }
    @Test
    fun `defenderTakesCards to false when phase is DEFENDING and bout IS defended`() {
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.DIAMONDS, Rank.KING))

        val attackerBefore = game.attackingPlayer

        game.pass()
        assertNotEquals(attackerBefore, game.attackingPlayer)
    }
    @Test
    fun `defenderTakesCards to false when phase is ATTACKING and bout IS NOT defended`() {
        game.setPhaseForTest(GamePhase.ATTACKING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.clearDefenseDeck()

        val attackerBefore = game.attackingPlayer

        game.pass()
        assertNotEquals(attackerBefore, game.attackingPlayer)
    }
    @Test
    fun `defenderTakesCards to false when phase is ATTACKING and bout IS defended`() {
        game.setPhaseForTest(GamePhase.ATTACKING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.DIAMONDS, Rank.KING))

        val attackerBefore = game.attackingPlayer

        game.pass()
        assertNotEquals(attackerBefore, game.attackingPlayer)
    }
    @Test
    fun `defender takes all bout cards when round lost`(){
        val defender = game.defendingPlayer
        defender.clearHand()

        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.QUEEN))

        game.finishBout(false)
        assertEquals(defender.hand().size, 3)
    }
    @Test
    fun `bout gets cleared when defender succeeds`(){
        val attackDeck = game.bout.attackDeck()
        val defenseDeck = game.bout.defenseDeck()
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.QUEEN))

        game.finishBout(true)
        assertEquals(attackDeck.size, 0)
        assertEquals(defenseDeck.size, 0)
    }

    @Test
    fun `isBoutDefended is true if sizes equal and not empty`() {
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.QUEEN))

        assertEquals(true, game.bout.isBoutDefended())
    }
    @Test
    fun `isBoutDefended is false if both sizes empty`() {
        assertEquals(false, game.bout.isBoutDefended())
    }
    @Test
    fun `isBoutDefended is true if sizes not equal`() {
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.QUEEN))

        assertEquals(false, game.bout.isBoutDefended())
    }
}