package hwr.oop.examples.template.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class PassTest {
    val game = Game(1, listOf("Alice", "Bob", "Charlie"))
    val player = game.players[0]

    @Test
    fun `pass next attacker is defender when defender takes cards`(){
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.defenseDeck.clear()

        game.pass()
        assertEquals(game.players[2], game.currentAttacker())
    }
    @Test
    fun `pass next attacker is defender`(){
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.defenseDeck.add(Card(Suit.CLUBS, Rank.QUEEN))

        game.pass()
        assertEquals(game.players[1], game.currentAttacker())
    }
    @Test
    fun `defenderTakesCards is true when phase is DEFENDING and bout is not defended`(){
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.defenseDeck.clear()

        game.pass()
        assertEquals(game.players[2], game.currentAttacker())
        assertEquals(game.players[0], game.currentDefender())
    }
    @Test
    fun `pass sets defenderTakesCards to false when phase is DEFENDING and bout IS defended`() {
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.defenseDeck.add(Card(Suit.DIAMONDS, Rank.KING))

        val attackerBefore = game.currentAttacker()

        game.pass()
        assertNotEquals(attackerBefore, game.currentAttacker())
    }
    @Test
    fun `pass sets defenderTakesCards to false when phase is ATTACKING and bout IS NOT defended`() {
        game.setPhaseForTest(GamePhase.ATTACKING)
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.defenseDeck.clear()

        val attackerBefore = game.currentAttacker()

        game.pass()
        assertNotEquals(attackerBefore, game.currentAttacker())
    }
    @Test
    fun `pass sets defenderTakesCards to false when phase is ATTACKING and bout IS defended`() {
        game.setPhaseForTest(GamePhase.ATTACKING)
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.defenseDeck.add(Card(Suit.DIAMONDS, Rank.KING))

        val attackerBefore = game.currentAttacker()

        game.pass()
        assertNotEquals(attackerBefore, game.currentAttacker())
    }
    @Test
    fun `defender takes all bout cards when round lost`(){
        val defender = game.currentDefender()
        defender.hand.clear()

        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.defenseDeck.add(Card(Suit.CLUBS, Rank.QUEEN))

        game.finishBout(false)
        assertEquals(defender.hand.size, 3)
    }
    @Test
    fun `bout gets cleared when defender succeeds`(){
        val attackDeck = game.bout.attackDeck
        val defenseDeck = game.bout.defenseDeck
        attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        defenseDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        defenseDeck.add(Card(Suit.CLUBS, Rank.QUEEN))

        game.finishBout(true)
        assertEquals(attackDeck.size, 0)
        assertEquals(defenseDeck.size, 0)
    }

    @Test
    fun `isBoutDefended is true if sizes equal and not empty`() {
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.defenseDeck.add(Card(Suit.CLUBS, Rank.QUEEN))

        assertEquals(true, game.isBoutDefended())
    }
    @Test
    fun `isBoutDefended is false if both sizes empty`() {
        assertEquals(false, game.isBoutDefended())
    }
    @Test
    fun `isBoutDefended is true if sizes not equal`() {
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.attackDeck.add(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.defenseDeck.add(Card(Suit.CLUBS, Rank.QUEEN))

        assertEquals(false, game.isBoutDefended())
    }
}