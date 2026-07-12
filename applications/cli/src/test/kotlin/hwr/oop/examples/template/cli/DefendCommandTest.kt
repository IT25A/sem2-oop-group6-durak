package hwr.oop.examples.template.cli

import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.obj
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.testing.test
import hwr.oop.examples.template.adapters.`in`.LoadGameByIdQuery
import hwr.oop.examples.template.adapters.`in`.NewGameUseCase
import hwr.oop.examples.template.adapters.`in`.PlayCardUseCase
import hwr.oop.examples.template.core.Card
import hwr.oop.examples.template.core.Deck
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.core.Rank
import hwr.oop.examples.template.core.Suit
import hwr.oop.examples.template.ports.out.GameRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DefendCommandTest {

    private fun fixedDeck(): Deck {
        val deck = Deck(mutableListOf<Card>().apply {
            add(Card(Suit.CLUBS,    Rank.SEVEN))
            add(Card(Suit.DIAMONDS, Rank.NINE))
            add(Card(Suit.SPADES,   Rank.JACK))
            add(Card(Suit.HEARTS,   Rank.SEVEN))
            add(Card(Suit.DIAMONDS, Rank.SIX))
            add(Card(Suit.SPADES,   Rank.SIX))
            add(Card(Suit.CLUBS,    Rank.EIGHT))
            add(Card(Suit.DIAMONDS, Rank.TEN))
            add(Card(Suit.SPADES,   Rank.QUEEN))
            add(Card(Suit.SPADES,   Rank.KING))
            add(Card(Suit.CLUBS,    Rank.ACE))
            add(Card(Suit.CLUBS,    Rank.KING))
            add(Card(Suit.HEARTS,   Rank.SIX))
        })
        return deck
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
    fun `defend command exits with status 0 when defender plays a valid card`() {
        val game = Game.createGameFromDeck(playerNames = listOf("Alice", "Bob"), deck = fixedDeck())
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)

        val attackCard = Card(Suit.CLUBS, Rank.SEVEN)
        val defendCard = Card(Suit.CLUBS, Rank.EIGHT)
        val attacker = game.attackingPlayer
        val defender = game.defendingPlayer

        ExampleBaseCommand()
            .apply { context { obj = cliContext } }
            .subcommands(OnGameIdCommand().subcommands(AttackCommand(), DefendCommand()))
            .test("onGameID ${game.gameId.value} attack ${attacker.name} ${attackCard.toShortString()}")

        val result = ExampleBaseCommand()
            .apply { context { obj = cliContext } }
            .subcommands(OnGameIdCommand().subcommands(AttackCommand(), DefendCommand()))
            .test("onGameID ${game.gameId.value} defend ${defender.name} ${defendCard.toShortString()}")

        assertThat(result.statusCode).isEqualTo(0)
    }

    @Test
    fun `defend command fails when wrong player name is given`() {
        val game = Game.createGameFromDeck(playerNames = listOf("Alice", "Bob"), deck = fixedDeck())
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, game.gameId.value)

        val attackCard = Card(Suit.CLUBS, Rank.SEVEN)
        ExampleBaseCommand()
            .apply { context { obj = cliContext } }
            .subcommands(OnGameIdCommand().subcommands(AttackCommand(), DefendCommand()))
            .test("onGameID ${game.gameId.value} attack ${game.attackingPlayer.name} ${attackCard.toShortString()}")

        val wrongPlayer = game.attackingPlayer.name
        val defendCard = Card(Suit.CLUBS, Rank.EIGHT)

        assertThrows<IllegalArgumentException> {
            ExampleBaseCommand()
                .apply { context { obj = cliContext } }
                .subcommands(OnGameIdCommand().subcommands(DefendCommand()))
                .test("onGameID ${game.gameId.value} defend $wrongPlayer ${defendCard.toShortString()}")
        }
    }

    @Test
    fun `defend command fails when no game id is set`() {
        val game = Game.createGameFromDeck(playerNames = listOf("Alice", "Bob"), deck = fixedDeck())
        val storage = makeStorage(game)
        val cliContext = makeContext(storage, null)

        val card = Card(Suit.CLUBS, Rank.EIGHT)

        assertThrows<IllegalArgumentException> {
            ExampleBaseCommand()
                .apply { context { obj = cliContext } }
                .subcommands(DefendCommand())
                .test("defend ${game.defendingPlayer.name} ${card.toShortString()}")
        }
    }
}