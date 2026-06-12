package hwr.oop.examples.template.ports.out
import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId

interface Persistence {

    fun getGame(id: GameId): Game
    fun save(game: Game): Unit

}