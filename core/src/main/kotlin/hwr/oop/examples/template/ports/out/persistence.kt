package hwr.oop.examples.template.ports.out

import hwr.oop.examples.template.core.Game
import java.util.UUID

interface Persistence {

    fun getGame(id: UUID): Game
    fun save(game: Game): Unit

}