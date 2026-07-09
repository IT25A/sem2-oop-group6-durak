package hwr.oop.examples.template.adapters.`in`

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.ports.out.SaveGamePort

class NewGameUseCase(private val saveGamePort: SaveGamePort) {
    fun newGame(playerNames: List<String>): Game {
        val game = Game.createRandomGame(playerNames = playerNames)
        saveGamePort.save(game)
        return game
    }
}