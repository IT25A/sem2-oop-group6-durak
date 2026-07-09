package hwr.oop.examples.template.adapters.`in`

import hwr.oop.examples.template.adapters.out.InMemoryPersistence
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoadGameByIdQueryTest {
    private lateinit var repository: InMemoryPersistence
    private lateinit var sut: LoadGameByIdQuery

    @BeforeEach
    fun setUp() {
        repository = InMemoryPersistence()
        sut = LoadGameByIdQuery(loadGameByIdPort = repository)
    }

    @Test
    fun `load returns the game that was saved`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        repository.save(game)

        val result = sut.load(game.gameId)

        assertThat(result.gameId).isEqualTo(game.gameId)
    }

    @Test
    fun `load throws when game does not exist`() {
        val unknownId = GameId.random()

        assertThrows(NoSuchElementException::class.java) {
            sut.load(unknownId)
        }
    }
}
