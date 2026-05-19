package hwr.oop.examples.template.core

class Graveyard {

}

fun main() {
	val game = Game(listOf("Bob", "Alice", "Max", "Tom", "John", "James"))
//	println("Trump card: ${game.deck.peekTrump()}")
    game.players[1].hand.clear()
    game.determineNextTurn()
    println(game.attackerIndex)
    println(game.defenderIndex)

//	game.attackPhase(true)
//	game.defensePhase()
//	game.attackPhase(false)
//	game.defensePhase()
}