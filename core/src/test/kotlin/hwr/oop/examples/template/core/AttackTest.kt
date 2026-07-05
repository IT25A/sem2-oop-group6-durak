package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AttackTest {
    private lateinit var game: Game
    private lateinit var attacker: Player
    private lateinit var bout: Bout

    @BeforeEach
    fun setUp() {
        game = Game.createRandomGame(
            playerNames = listOf("Alice", "Bob", "Charlie")
        )
        attacker = game.attackingPlayer
        bout = game.bout
    }

    @Test
    fun `attack throws on invalid gamePhase`(){
        game.setGamePhase(GamePhase.DEFENDING)
        val exception = assertThrows(InvalidMoveException::class.java){
            game.attack(Card(Suit.SPADES, Rank.QUEEN))
        }
        assertTrue(exception.message!!.contains("Can not attack"))
    }
    @Test
    fun `attack throws on invalid card`(){
        attacker.clearHand()
        attacker.addToHand(Card(Suit.DIAMONDS, Rank.QUEEN))
        val exception = assertThrows(UnavailableCardException::class.java){
            game.attack(Card(Suit.SPADES, Rank.QUEEN))
        }
        assertTrue(exception.message!!.contains("Card is not part of"))
    }
    @Test
    fun `attack throws on illegal card move`(){
        game.setGamePhase(GamePhase.ATTACKING)

        bout.addAttackCard(Card(Suit.DIAMONDS, Rank.QUEEN))
        bout.addDefenseCard(Card(Suit.DIAMONDS, Rank.KING))

        val illegalCard = Card(Suit.SPADES, Rank.SIX)
        attacker.addToHand(illegalCard)

        val exception = assertThrows(InvalidCardException::class.java){
            game.attack(illegalCard)
        }
        assertTrue(exception.message!!.contains("You are not allowed"))
    }

    @Test
    fun `attack allowed when attackDeck is empty`() {
        game.setGamePhase(GamePhase.ATTACKING)
        attacker.clearHand()

        val card = Card(Suit.CLUBS, Rank.JACK)
        attacker.addToHand(card)

        game.attack(card)
        assertThat(game.bout.getAttackDeck()).contains(card)
    }
    @Test
    fun `attack allowed when attackDeck contains rank`() {
        game.setGamePhase(GamePhase.ATTACKING)
        attacker.clearHand()

        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))

        val card = Card(Suit.SPADES, Rank.QUEEN)
        attacker.addToHand(card)

        game.attack(card)
        assertThat(game.bout.getAttackDeck()).contains(card)
    }
    @Test
    fun `attack throws when rank only in defenseDeck is absent from attackDeck`() {
        game.setGamePhase(GamePhase.ATTACKING)
        attacker.clearHand()

        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.KING))

        val illegalCard = Card(Suit.SPADES, Rank.JACK)
        attacker.addToHand(illegalCard)

        assertThrows(InvalidCardException::class.java) {
            game.attack(illegalCard)
        }
    }
    @Test
    fun `attack allowed when defenseDeck contains rank`() {
        game.setGamePhase(GamePhase.ATTACKING)
        attacker.clearHand()

        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.KING))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.QUEEN))

        val card = Card(Suit.SPADES, Rank.QUEEN)
        attacker.addToHand(card)

        game.attack(card)
        assertThat(game.bout.getAttackDeck()).contains(card)
    }
    @Test
    fun `attack throws when rank absent from both decks and decks non-empty`() {
        game.setGamePhase(GamePhase.ATTACKING)
        attacker.clearHand()

        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.KING))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.ACE))

        val illegalCard = Card(Suit.SPADES, Rank.SIX)
        attacker.addToHand(illegalCard)

        assertThrows(InvalidCardException::class.java) {
            game.attack(illegalCard)
        }
    }
    @Test
    fun `attack throws when ranks do not match and decks are not empty`() {
        game.setGamePhase(GamePhase.ATTACKING)
        attacker.clearHand()

        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.KING))

        val illegalCard = Card(Suit.CLUBS, Rank.JACK)
        attacker.addToHand(illegalCard)

        assertThrows(InvalidCardException::class.java) {
            game.attack(illegalCard)
        }
    }
    @Test
    fun `attack adds card to attackDeck`() {
        val attackCard = Card(Suit.HEARTS, Rank.JACK)
        game.setGamePhase(GamePhase.ATTACKING)
        attacker.addToHand(attackCard)
        game.attack(attackCard)
        assertThat(bout.getAttackDeck()).contains(attackCard)
    }
    @Test
    fun `attack removes card from attackers hand`(){
        val attackCard = Card(Suit.HEARTS, Rank.JACK)
        game.setGamePhase(GamePhase.ATTACKING)
        attacker.clearHand()
        attacker.addToHand(attackCard)
        game.attack(attackCard)
        assertThat(attacker.getHand()).doesNotContain(attackCard)
    }
    @Test
    fun `attack adds attacker to win order when last card is played and deck is empty`() {
        val attackCard = Card(Suit.HEARTS, Rank.SIX)

        attacker.clearHand()
        attacker.addToHand(attackCard)

        game.deck.clearDeck()
        game.bout.clearBout()
        game.setGamePhase(GamePhase.ATTACKING)

        game.attack(attackCard)

        assertThat(attacker.getHand()).isEmpty()
        assertThat(game.getPlayerWinOrder()).contains(attacker)
    }
}