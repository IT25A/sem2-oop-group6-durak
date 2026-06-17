package hwr.oop.examples.template.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DefendTest {
    private lateinit var game: Game
    private lateinit var defender: Player
    private lateinit var trump: Suit
    private lateinit var bout: Bout

    @BeforeEach
    fun setUp() {
        val deck = Deck.createShuffled()
        deck.clearDeckForTest()
        repeat(18) { deck.addCardToDeckForTest(Card(Suit.HEARTS, Rank.SIX)) }
        deck.addCardToDeckForTest(Card(Suit.CLUBS, Rank.ACE))
        game = Game.createGameFromDeck(
            gameId = GameId.random(),
            playerNames = listOf("Alice", "Bob", "Charlie"),
            deck = deck
        )
        defender = game.defendingPlayer
        trump = game.trump
        bout = game.bout
    }

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
        defender.clearHand()
        defender.addToHand(Card(Suit.DIAMONDS, Rank.QUEEN))
        val exception = assertThrows(IllegalArgumentException::class.java){
            game.defend(Card(Suit.SPADES, Rank.QUEEN))
        }
        assertTrue(exception.message!!.contains("Card is not part of"))
    }
    @Test
    fun `defend() throws on illegal card move`(){
        game.setPhaseForTest(GamePhase.DEFENDING)

        bout.addAttackCard(Card(Suit.DIAMONDS, Rank.QUEEN))
        bout.clearDefenseDeck()

        val illegalCard = Card(Suit.SPADES, Rank.SIX)
        defender.addToHand(illegalCard)

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

        bout.addAttackCard(attackCard)
        defender.clearHand()
        defender.addToHand(defendCard)

        game.defend(defendCard)
    }
    @Test
    fun `defense with higher same suit succeeds`() {
        game.setPhaseForTest(GamePhase.DEFENDING)

        val attackCard = Card(Suit.HEARTS, Rank.NINE)
        val defendCard = Card(Suit.HEARTS, Rank.JACK)

        bout.addAttackCard(attackCard)
        defender.clearHand()
        defender.addToHand(defendCard)

        game.defend(defendCard)
    }
    @Test
    fun `defense fails when attacking card is trump but defense is not trump`() {
        game.setPhaseForTest(GamePhase.DEFENDING)

        val attackCard = Card(trump, Rank.NINE)
        val defendCard = Card(Suit.HEARTS, Rank.KING)

        bout.addAttackCard(attackCard)
        defender.clearHand()
        defender.addToHand(defendCard)

        assertThrows(IllegalArgumentException::class.java) {
            game.defend(defendCard)
        }
    }
    @Test
    fun `defense fails when same suit but lower or equal rank`() {
        game.setPhaseForTest(GamePhase.DEFENDING)

        val attackCard = Card(Suit.HEARTS, Rank.JACK)
        val defendCard = Card(Suit.HEARTS, Rank.NINE)

        bout.addAttackCard(attackCard)
        defender.clearHand()
        defender.addToHand(defendCard)

        assertThrows(IllegalArgumentException::class.java) {
            game.defend(defendCard)
        }
    }
    @Test
    fun `defense fails when different suit and defense is not trump`() {
        game.setPhaseForTest(GamePhase.DEFENDING)

        val attackCard = Card(Suit.HEARTS, Rank.EIGHT)
        val defendCard = Card(Suit.SPADES, Rank.KING)

        bout.addAttackCard(attackCard)
        defender.clearHand()
        defender.addToHand(defendCard)

        assertThrows(IllegalArgumentException::class.java) {
            game.defend(defendCard)
        }
    }
}