package hwr.oop.examples.template.core

//class Graveyard {
//
//}

fun main() {
	val game = Game(1,listOf("Bob", "Alice", "Max", "Tom", "John", "James"))
    game.players[1].hand.clear()
    game.determineNextTurn(game.players[0])
    println(game.attackerIndex)
    println(game.defenderIndex)
//	game.attackPhase(true)
//	game.defensePhase()
//	game.attackPhase(false)
//	game.defensePhase()
}