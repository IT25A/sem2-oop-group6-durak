package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.obj
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.testing.test
import hwr.oop.examples.template.adapters.`in`.LoadGameByIdQuery
import hwr.oop.examples.template.adapters.`in`.NewGameUseCase
import hwr.oop.examples.template.adapters.`in`.PlayCardUseCase
import hwr.oop.examples.template.core.Card
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.core.Rank
import hwr.oop.examples.template.ports.out.GameRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AttackCommandTest {

    private fun Card.toCliString(): String {
        val rankStr = when (rank) {
            Rank.SIX -> "6"
            Rank.SEVEN -> "7"
            Rank.EIGHT -> "8"
            Rank.NINE -> "9"
            Rank.TEN -> "10"
            Rank.JACK -> "J"
            Rank.QUEEN -> "Q"
            Rank.KING -> "K"
            Rank.ACE -> "A"
        }
        val suitChar = suit.name.first()
        return "$rankStr$suitChar"
    }

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
    fun `attack command exits with status 0 when attacker plays a valid card`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)

        val attacker = game.attackingPlayer
        val card = attacker.getHand().first()

        val result = ExampleBaseCommand()
            .apply { context { obj = cliContext } }
            .subcommands(OnGameIdCommand().subcommands(AttackCommand()))
            .test("onGameID ${game.gameId.value} attack ${attacker.name} ${card.toCliString()}")

        assertThat(result.statusCode).isEqualTo(0)
    }

    @Test
    fun `attack command fails when wrong player name is given`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)

        val card = game.attackingPlayer.getHand().first()
        val wrongPlayer = game.defendingPlayer.name

        assertThrows<IllegalArgumentException> {
            ExampleBaseCommand()
                .apply { context { obj = cliContext } }
                .subcommands(OnGameIdCommand().subcommands(AttackCommand()))
                .test("onGameID ${game.gameId.value} attack $wrongPlayer ${card.toCliString()}")
        }
    }

    @Test
    fun `attack command fails when no game id is set`() {
        val game = Game.createRandomGame(playerNames = listOf("Alice", "Bob"))
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, null)

        val card = game.attackingPlayer.getHand().first()

        assertThrows<IllegalArgumentException> {
            ExampleBaseCommand()
                .apply { context { obj = cliContext } }
                .subcommands(AttackCommand())
                .test("attack ${game.attackingPlayer.name} ${card.toCliString()}")
        }
    }
}