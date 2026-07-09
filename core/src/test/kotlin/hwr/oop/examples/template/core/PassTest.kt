package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
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
        val defenderIndex = game.players.indexOf(game.defendingPlayer)
        val expectedNextAttacker = game.players[(defenderIndex + 1) % game.players.size]
        game.setGamePhase(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.clearDefenseDeck()

        game.pass()
        assertEquals(expectedNextAttacker, game.attackingPlayer)
    }
    @Test
    fun `pass next attacker is defender`(){
        val defenderBefore = game.defendingPlayer
        game.setGamePhase(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.QUEEN))

        game.pass()
        assertEquals(defenderBefore, game.attackingPlayer)
    }
    @Test
    fun `defenderTakesCards is true when phase is DEFENDING and bout is NOT defended`(){
        val defenderIndex = game.players.indexOf(game.defendingPlayer)
        val expectedNextAttacker = game.players[(defenderIndex + 1) % game.players.size]
        val expectedNextDefender = game.players[(game.players.indexOf(expectedNextAttacker) + 1) % game.players.size]
        game.setGamePhase(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.clearDefenseDeck()

        game.pass()
        assertEquals(expectedNextAttacker, game.attackingPlayer)
        assertEquals(expectedNextDefender, game.defendingPlayer)
    }
    @Test
    fun `defenderTakesCards to false when phase is DEFENDING and bout IS defended`() {
        game.setGamePhase(GamePhase.DEFENDING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.DIAMONDS, Rank.KING))

        val attackerBefore = game.attackingPlayer

        game.pass()
        assertNotEquals(attackerBefore, game.attackingPlayer)
    }
    @Test
    fun `defenderTakesCards to false when phase is ATTACKING and bout IS NOT defended`() {
        game.setGamePhase(GamePhase.ATTACKING)
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.clearDefenseDeck()

        val attackerBefore = game.attackingPlayer

        game.pass()
        assertNotEquals(attackerBefore, game.attackingPlayer)
    }
    @Test
    fun `defenderTakesCards to false when phase is ATTACKING and bout IS defended`() {
        game.setGamePhase(GamePhase.ATTACKING)
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
        assertEquals(defender.getHand().size, 3)
    }
    @Test
    fun `pass gives defender bout cards when defender takes`() {
        val defender = game.defendingPlayer

        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.SEVEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.KING))

        game.setGamePhase(GamePhase.DEFENDING)

        game.pass()

        assertThat(defender.getHand().size).isEqualTo(9)
    }
    @Test
    fun `pass clears bout and refills hands to 6`() {
        val attacker = game.attackingPlayer
        val defender = game.defendingPlayer

        attacker.clearHand()
        defender.clearHand()

        attacker.addToHand(Card(Suit.CLUBS, Rank.SIX))
        attacker.addToHand(Card(Suit.CLUBS, Rank.SEVEN))
        attacker.addToHand(Card(Suit.CLUBS, Rank.EIGHT))

        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.KING))

        game.setGamePhase(GamePhase.DEFENDING)

        game.deck.clearDeck()
        game.deck.addCardToDeck(Card(Suit.CLUBS, Rank.JACK))
        game.deck.addCardToDeck(Card(Suit.CLUBS, Rank.QUEEN))
        game.deck.addCardToDeck(Card(Suit.CLUBS, Rank.KING))

        game.pass()

        assertThat(game.bout.getAttackDeck()).isEmpty()
        assertThat(game.bout.getDefenseDeck()).isEmpty()

        assertThat(attacker.getHand()).hasSize(6)
    }
    @Test
    fun `pass clears bout on defended bout - minimal`() {
        game.bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        game.bout.addDefenseCard(Card(Suit.CLUBS, Rank.KING))

        game.setGamePhase(GamePhase.DEFENDING)

        // Pre-check: bout has cards
        val beforeAttackSize = game.bout.getAttackDeck().size
        val beforeDefenseSize = game.bout.getDefenseDeck().size
        assertThat(beforeAttackSize).isEqualTo(1)
        assertThat(beforeDefenseSize).isEqualTo(1)

        game.attackingPlayer.clearHand()
        game.defendingPlayer.clearHand()

        game.deck.clearDeck()
        game.deck.addCardToDeck(Card(Suit.CLUBS, Rank.JACK))

        game.pass()

        // Post-check: bout must be empty
        assertThat(game.bout.getAttackDeck().size).isEqualTo(0)
        assertThat(game.bout.getDefenseDeck().size).isEqualTo(0)
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