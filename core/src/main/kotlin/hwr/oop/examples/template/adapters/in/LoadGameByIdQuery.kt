package hwr.oop.examples.template.adapters.`in`

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId
import hwr.oop.examples.template.ports.out.LoadGameByIdPort

class LoadGameByIdQuery(private val loadGameByIdPort: LoadGameByIdPort) {
    fun load(gameId: GameId): Game = loadGameByIdPort.loadById(gameId)
}