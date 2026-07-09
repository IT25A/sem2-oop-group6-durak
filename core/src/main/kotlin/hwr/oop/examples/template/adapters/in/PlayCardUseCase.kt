package hwr.oop.examples.template.adapters.`in`

import hwr.oop.examples.template.core.Card
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.ports.out.LoadGameByIdPort
import hwr.oop.examples.template.ports.out.SaveGamePort

class PlayCardUseCase(
    private val loadGameByIdPort: LoadGameByIdPort,
    private val saveGamePort: SaveGamePort,
) {
    fun attack(gameId: GameId, card: Card): Game {
        val game = loadGameByIdPort.loadById(gameId)
        game.attack(card)
        saveGamePort.save(game)
        return game
    }

    fun defend(gameId: GameId, card: Card): Game {
        val game = loadGameByIdPort.loadById(gameId)
        game.defend(card)
        saveGamePort.save(game)
        return game
    }

    fun pass(gameId: GameId): Game {
        val game = loadGameByIdPort.loadById(gameId)
        game.pass()
        saveGamePort.save(game)
        return game
    }
}