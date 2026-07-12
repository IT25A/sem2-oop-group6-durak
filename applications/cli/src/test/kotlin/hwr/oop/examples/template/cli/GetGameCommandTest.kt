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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetGameCommandTest {

    private fun makeStorage(game: Game) = mutableMapOf(game.gameId.value to game)

    private fun makeContext(storage: MutableMap<String, Game>, gameId: String?): CliContext {
        val persistence = object : GameRepository {
            override fun loadById(id: GameId) =
                storage[id.value] ?: error("Game not found")
            override fun save(game: Game) { storage[game.gameId.value] = game }
        }
        return CliContext(
            newGameUseCase = NewGameUseCase(persistence),
            playCardUseCase = PlayCardUseCase(persistence, persistence),
            loadGameByIdQuery = LoadGameByIdQuery(persistence),
            gameId = gameId,
        )
    }

    @Test
    fun `getGame command exits with status 0 and prints game state`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)

        val result = ExampleBaseCommand()
            .apply { context { obj = cliContext } }
            .subcommands(OnGameIdCommand().subcommands(GetGameCommand()))
            .test("onGameID ${game.gameId.value} getGame")

        assertThat(result.statusCode).isEqualTo(0)
        assertThat(result.output).contains(game.gameId.value)
    }

    @Test
    fun `getGame command output contains attacker and defender names`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)

        val result = ExampleBaseCommand()
            .apply { context { obj = cliContext } }
            .subcommands(OnGameIdCommand().subcommands(GetGameCommand()))
            .test("onGameID ${game.gameId.value} getGame")

        assertThat(result.output).contains(game.attackingPlayer.name)
        assertThat(result.output).contains(game.defendingPlayer.name)
    }

    @Test
    fun `getGame command fails when no game id is set`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, null)

        assertThrows<IllegalArgumentException> {
            ExampleBaseCommand()
                .apply { context { obj = cliContext } }
                .subcommands(GetGameCommand())
                .test("getGame")
        }
    }
}