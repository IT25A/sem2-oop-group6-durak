package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.obj
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.testing.test
import hwr.oop.examples.template.adapters.`in`.LoadGameByIdQuery
import hwr.oop.examples.template.adapters.`in`.NewGameUseCase
import hwr.oop.examples.template.adapters.`in`.PlayCardUseCase
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.ports.out.GameRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StartGameCommandTest {

    private fun makeStorage() = mutableMapOf<String, Game>()

    private fun makeContext(storage: MutableMap<String, Game>): CliContext {
        val persistence = object : GameRepository {
            override fun loadById(id: GameId) =
                storage[id.value] ?: error("Game not found")

            override fun save(game: Game) {
                storage[game.gameId.value] = game
            }
        }

        return CliContext(
            newGameUseCase = NewGameUseCase(persistence),
            playCardUseCase = PlayCardUseCase(persistence, persistence),
            loadGameByIdQuery = LoadGameByIdQuery(persistence),
            gameId = null
        )
    }

    @Test
    fun `startGame creates a new Game`() {
        val storage = makeStorage()
        assertEquals(storage.size, 0)
        val cliContext =  makeContext(storage)
        ExampleBaseCommand()
            .apply { context { obj = cliContext } }
            .subcommands(StartGameCommand())
            .test("startGame Alice Bob")
        assertEquals(storage.size, 1)
    }
}