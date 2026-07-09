package hwr.oop.examples.template.ports.out

import hwr.oop.examples.template.core.Game
import hwr.oop.examples.template.core.GameId

interface LoadGameByIdPort {
    fun loadById(id: GameId): Game
}