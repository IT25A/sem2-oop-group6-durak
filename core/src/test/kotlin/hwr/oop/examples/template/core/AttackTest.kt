package hwr.oop.examples.template.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AttackTest {
    val game = Game(1, listOf("Alice", "Bob", "Charlie"))
    val bout = game.bout

    @Test
    fun `attack() throws on invalid gamePhase`(){
        game.setPhaseForTest(GamePhase.DEFENDING)
        val exception = assertThrows(InvalidMoveException::class.java){
            game.attack(Card(Suit.SPADES, Rank.QUEEN))
        }
        assertTrue(exception.message!!.contains("Can not attack"))
    }
    @Test
    fun `attack() throws on invalid card`(){
        game.currentAttacker().hand.clear()
        game.currentAttacker().hand.add(Card(Suit.DIAMONDS, Rank.QUEEN))
        val exception = assertThrows(IllegalArgumentException::class.java){
            game.attack(Card(Suit.SPADES, Rank.QUEEN))
        }
        assertTrue(exception.message!!.contains("Card is not part of"))
    }
    @Test
    fun `attack() throws on illegal card move`(){
        game.setPhaseForTest(GamePhase.ATTACKING)

        bout.attackDeck.add(Card(Suit.DIAMONDS, Rank.QUEEN))
        bout.defenseDeck.add(Card(Suit.DIAMONDS, Rank.KING))

        val illegalCard = Card(Suit.SPADES, Rank.SIX)
        game.currentAttacker().hand.add(illegalCard)

        val exception = assertThrows(IllegalArgumentException::class.java){
            game.attack(illegalCard)
        }
        assertTrue(exception.message!!.contains("You are not allowed"))
    }
    @Test
    fun `attack() allowed when bout attackDeck is empty`() {
        game.setPhaseForTest(GamePhase.ATTACKING)
        game.bout.attackDeck.clear()
        game.bout.defenseDeck.clear()

        val attackCard = Card(Suit.HEARTS, Rank.SIX)
        game.currentAttacker().hand.add(attackCard)
        game.attack(attackCard)
    }
    @Test
    fun `attack() allowed when bout attackDeck contains rank`() {
        game.setPhaseForTest(GamePhase.ATTACKING)
        game.bout.attackDeck.add(Card(Suit.HEARTS, Rank.QUEEN))
        game.bout.defenseDeck.clear()

        val attackCard = Card(Suit.SPADES, Rank.QUEEN)
        game.currentAttacker().hand.add(attackCard)
        game.attack(attackCard)
    }
    @Test
    fun `attack() allowed when defenseDeck contains rank`() {
        game.setPhaseForTest(GamePhase.ATTACKING)
        game.bout.attackDeck.add(Card(Suit.HEARTS, Rank.JACK))
        game.bout.defenseDeck.add(Card(Suit.HEARTS, Rank.QUEEN))

        val attackCard = Card(Suit.SPADES, Rank.QUEEN)
        game.currentAttacker().hand.add(attackCard)
        game.attack(attackCard)
    }
}