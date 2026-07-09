package hwr.oop.examples.template.adapters.`in`

import hwr.oop.examples.template.adapters.out.InMemoryPersistence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NewGameUseCaseTest {
    private lateinit var repository: InMemoryPersistence
    private lateinit var sut: NewGameUseCase

    @BeforeEach
    fun setUp() {
        repository = InMemoryPersistence()
        sut = NewGameUseCase(saveGamePort = repository)
    }

    @Test
    fun `newGame returns a game with the given player names`() {
        val game = sut.newGame(listOf("Alice", "Bob"))
        assertThat(game.players.map { it.name }).containsExactly("Alice", "Bob")
    }

    @Test
    fun `newGame persists the game so it can be loaded back`() {
        val game = sut.newGame(listOf("Alice", "Bob"))
        assertThat(repository.savedGames.map { it.gameId }).contains(game.gameId)
    }

    @Test
    fun `newGame assigns each player 6 cards from the deck`() {
        val game = sut.newGame(listOf("Alice", "Bob"))
        assertThat(game.players).allSatisfy { assertThat(it.getHand()).hasSize(6) }
    }
}
