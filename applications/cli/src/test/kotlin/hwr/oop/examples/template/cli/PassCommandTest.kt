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
import hwr.oop.examples.template.core.GamePhase
import hwr.oop.examples.template.ports.out.GameRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PassCommandTest {

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
    fun `pass command exits with status 0 when a player passes successfully`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)

        val result = ExampleBaseCommand()
            .apply { context { obj = cliContext } }
            .subcommands(OnGameIdCommand().subcommands(PassCommand()))
            .test("onGameID ${game.gameId.value} pass ${game.attackingPlayer.name}")

        assertThat(result.statusCode).isEqualTo(0)
    }

    @Test
    fun `pass command fails when defending player passes during ATTACKING phase`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)

        val wrongPlayer = game.defendingPlayer.name

        assertThrows<IllegalArgumentException> {
            ExampleBaseCommand()
                .apply { context { obj = cliContext } }
                .subcommands(OnGameIdCommand().subcommands(PassCommand()))
                .test("onGameID ${game.gameId.value} pass $wrongPlayer")
        }
    }
    @Test
    fun `pass command fails when attacking player passes during DEFENDING phase`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)
        game.setGamePhase(GamePhase.DEFENDING)

        val wrongPlayer = game.attackingPlayer.name

        assertThrows<IllegalArgumentException> {
            ExampleBaseCommand()
                .apply { context { obj = cliContext } }
                .subcommands(OnGameIdCommand().subcommands(PassCommand()))
                .test("onGameID ${game.gameId.value} pass $wrongPlayer")
        }
    }
    @Test
    fun `pass command fails when any player passes during FINISHED phase`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)
        game.setGamePhase(GamePhase.FINISHED)

        assertThrows<IllegalArgumentException> {
            ExampleBaseCommand()
                .apply { context { obj = cliContext } }
                .subcommands(OnGameIdCommand().subcommands(PassCommand()))
                .test("onGameID ${game.gameId.value} pass ${game.defendingPlayer.name}")
        }
    }

    @Test
    fun `pass command fails when no game id is set`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, null)


        assertThrows<IllegalArgumentException> {
            ExampleBaseCommand()
                .apply { context { obj = cliContext } }
                .subcommands(PassCommand())
                .test("pass ${game.attackingPlayer.name}")
        }
    }
}