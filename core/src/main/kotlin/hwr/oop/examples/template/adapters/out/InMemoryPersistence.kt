package hwr.oop.examples.template.adapters.out

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.ports.out.LoadGameByIdPort
import hwr.oop.examples.template.ports.out.SaveGamePort

internal class InMemoryPersistence : SaveGamePort, LoadGameByIdPort {
    private val map = mutableMapOf<String, Game>()
    val savedGames = mutableListOf<Game>()

    override fun save(game: Game) {
        map[game.gameId.value] = game
        savedGames.add(game)
    }

    override fun loadById(id: GameId): Game =
        map[id.value] ?: throw NoSuchElementException("Game not found: ${id.value}")
}
