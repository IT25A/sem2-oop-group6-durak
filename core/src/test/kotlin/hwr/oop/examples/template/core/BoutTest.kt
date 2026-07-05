package hwr.oop.examples.template.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BoutTest {
    private lateinit var game: Game
    private lateinit var bout: Bout

    @BeforeEach
    fun setUp() {
        game = Game.createRandomGame(
            playerNames = listOf("Alice", "Bob", "Charlie"),
            gameId = GameId("1")
        )
        bout = game.bout
    }

    @Test
    fun `addAttackCard adds a card to attack deck`() {
        val card = Card(Suit.DIAMONDS, Rank.KING)
        bout.addAttackCard(card)
        assertThat(bout.getAttackDeck()).contains(card)
    }
    @Test
    fun `addDefenseCard adds a card to defense deck`() {
        val card = Card(Suit.DIAMONDS, Rank.KING)
        bout.addDefenseCard(card)
        assertThat(bout.getDefenseDeck()).contains(card)
    }
    @Test
    fun `clearDefenseDeck clears defense deck`() {
        bout.addDefenseCard(Card(Suit.DIAMONDS, Rank.KING))
        bout.clearDefenseDeck()
        assertThat(bout.getDefenseDeck()).isEmpty()
    }
    @Test
    fun `clearBout clears both decks`() {
        bout.addAttackCard(Card(Suit.CLUBS, Rank.QUEEN))
        bout.addAttackCard(Card(Suit.HEARTS, Rank.JACK))
        bout.addDefenseCard(Card(Suit.DIAMONDS, Rank.KING))

        bout.clearBout()

        assertThat(bout.getAttackDeck()).isEmpty()
        assertThat(bout.getDefenseDeck()).isEmpty()
    }
}