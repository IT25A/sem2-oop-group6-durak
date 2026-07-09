package hwr.oop.examples.template.adapters.`in`

import hwr.oop.examples.template.adapters.out.InMemoryPersistence
import hwr.oop.examples.template.core.Card
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.Rank
import hwr.oop.examples.template.core.Suit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayCardUseCaseTest {
    private lateinit var repository: InMemoryPersistence
    private lateinit var sut: PlayCardUseCase

    @BeforeEach
    fun setUp() {
        repository = InMemoryPersistence()
        sut = PlayCardUseCase(loadGameByIdPort = repository, saveGamePort = repository)
    }

    @Test
    fun `attack places card on the table`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val attackCard = game.attackingPlayer.getHand().first()
        repository.save(game)

        val result = sut.attack(game.gameId, attackCard)

        assertThat(result.bout.getAttackDeck()).contains(attackCard)
    }

    @Test
    fun `attack removes the card from the attacker's hand`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val attackCard = game.attackingPlayer.getHand().first()
        repository.save(game)

        val result = sut.attack(game.gameId, attackCard)

        assertThat(result.attackingPlayer.getHand()).doesNotContain(attackCard)
    }

    @Test
    fun `attack persists the updated game`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val attackCard = game.attackingPlayer.getHand().first()
        repository.save(game)
        val saveCountBefore = repository.savedGames.size

        sut.attack(game.gameId, attackCard)

        assertThat(repository.savedGames).hasSizeGreaterThan(saveCountBefore)
        assertThat(repository.savedGames.last().bout.getAttackDeck()).contains(attackCard)
    }

    @Test
    fun `defend places card in the defense deck`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val attacker = game.attackingPlayer
        val defender = game.defendingPlayer

        val attackCard = Card(Suit.CLUBS, Rank.SIX)
        attacker.clearHand()
        attacker.addToHand(attackCard)
        val defendCard = Card(game.trump, Rank.SEVEN)
        defender.clearHand()
        defender.addToHand(defendCard)
        game.attack(attackCard)
        repository.save(game)

        val result = sut.defend(game.gameId, defendCard)

        assertThat(result.bout.getDefenseDeck()).contains(defendCard)
    }

    @Test
    fun `defend persists the updated game`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val attacker = game.attackingPlayer
        val defender = game.defendingPlayer

        val attackCard = Card(Suit.CLUBS, Rank.SIX)
        attacker.clearHand()
        attacker.addToHand(attackCard)
        val defendCard = Card(game.trump, Rank.SEVEN)
        defender.clearHand()
        defender.addToHand(defendCard)
        game.attack(attackCard)

        repository.save(game)
        val saveCountBefore = repository.savedGames.size

        sut.defend(game.gameId, defendCard)

        assertThat(repository.savedGames).hasSizeGreaterThan(saveCountBefore)
        assertThat(repository.savedGames.last().bout.getDefenseDeck()).contains(defendCard)
    }

    @Test
    fun `pass clears the bout and advances the turn`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val attackerBefore = game.attackingPlayer
        repository.save(game)

        val result = sut.pass(game.gameId)

        assertThat(result.bout.getAttackDeck()).isEmpty()
        assertThat(result.attackingPlayer).isNotEqualTo(attackerBefore)
    }

    @Test
    fun `pass persists the updated game`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        repository.save(game)
        val saveCountBefore = repository.savedGames.size

        sut.pass(game.gameId)

        assertThat(repository.savedGames).hasSizeGreaterThan(saveCountBefore)
        assertThat(repository.savedGames.last().bout.getAttackDeck()).isEmpty()
    }
}
