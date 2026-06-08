package hwr.oop.examples.template.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DefendTest {
    val game = Game(1, listOf("Alice", "Bob", "Charlie"), Suit.CLUBS)
    val defender = game.currentDefender()
    val trump = game.trump
    val bout = game.bout

    @Test
    fun `defend() throws on invalid gamePhase`(){
        game.setPhaseForTest(GamePhase.ATTACKING)
        val exception = assertThrows(InvalidMoveException::class.java){
            game.defend(Card(Suit.SPADES, Rank.QUEEN))
        }
        assertTrue(exception.message!!.contains("Can not defend"))
    }
    @Test
    fun `defend() throws on invalid card`(){
        game.setPhaseForTest(GamePhase.DEFENDING)
        game.currentDefender().hand.clear()
        game.currentDefender().hand.add(Card(Suit.DIAMONDS, Rank.QUEEN))
        val exception = assertThrows(IllegalArgumentException::class.java){
            game.defend(Card(Suit.SPADES, Rank.QUEEN))
        }
        assertTrue(exception.message!!.contains("Card is not part of"))
    }
    @Test
    fun `defend() throws on illegal card move`(){
        game.setPhaseForTest(GamePhase.DEFENDING)

        bout.attackDeck.add(Card(Suit.DIAMONDS, Rank.QUEEN))
        bout.defenseDeck.clear()

        val illegalCard = Card(Suit.SPADES, Rank.SIX)
        game.currentDefender().hand.add(illegalCard)

        val exception = assertThrows(IllegalArgumentException::class.java){
            game.defend(illegalCard)
        }
        assertTrue(exception.message!!.contains("is not a valid card"))
    }
    @Test
    fun `defense with trump against non-trump attack succeeds`() {
        game.setPhaseForTest(GamePhase.DEFENDING)

        val attackCard = Card(Suit.HEARTS, Rank.NINE)
        val defendCard = Card(trump, Rank.SIX)

        bout.attackDeck.add(attackCard)
        defender.hand.clear()
        defender.hand.add(defendCard)

        game.defend(defendCard)
    }
    @Test
    fun `defense with higher same suit succeeds`() {
        game.setPhaseForTest(GamePhase.DEFENDING)

        val attackCard = Card(Suit.HEARTS, Rank.NINE)
        val defendCard = Card(Suit.HEARTS, Rank.JACK)

        bout.attackDeck.add(attackCard)
        defender.hand.clear()
        defender.hand.add(defendCard)

        game.defend(defendCard)
    }
    @Test
    fun `defense fails when attacking card is trump but defense is not trump`() {
        game.setPhaseForTest(GamePhase.DEFENDING)

        val attackCard = Card(trump, Rank.NINE)
        val defendCard = Card(Suit.HEARTS, Rank.KING)

        bout.attackDeck.add(attackCard)
        defender.hand.clear()
        defender.hand.add(defendCard)

        assertThrows(IllegalArgumentException::class.java) {
            game.defend(defendCard)
        }
    }
    @Test
    fun `defense fails when same suit but lower or equal rank`() {
        game.setPhaseForTest(GamePhase.DEFENDING)

        val attackCard = Card(Suit.HEARTS, Rank.JACK)
        val defendCard = Card(Suit.HEARTS, Rank.NINE)

        bout.attackDeck.add(attackCard)
        defender.hand.clear()
        defender.hand.add(defendCard)

        assertThrows(IllegalArgumentException::class.java) {
            game.defend(defendCard)
        }
    }
    @Test
    fun `defense fails when different suit and defense is not trump`() {
        game.setPhaseForTest(GamePhase.DEFENDING)

        val attackCard = Card(Suit.HEARTS, Rank.EIGHT)
        val defendCard = Card(Suit.SPADES, Rank.KING)

        bout.attackDeck.add(attackCard)
        defender.hand.clear()
        defender.hand.add(defendCard)

        assertThrows(IllegalArgumentException::class.java) {
            game.defend(defendCard)
        }
    }
}