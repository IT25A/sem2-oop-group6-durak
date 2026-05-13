package hwr.oop.examples.template.core

class Graveyard {

}

fun main() {
	val game = Game(listOf("Bob", "Alice"))
	println("Trump card: ${game.deck.peekTrump()}")

//	for (player in game.players) {
//		player.printHand()
//	}
//	println("Cards remaining: ${game.deck.remaining()}")
//	game.deck.printDeck()

	game.attackPhase(true)
	game.defensePhase()
	game.attackPhase(false)
	game.defensePhase()

//	player.printHand()
//	player2.printHand()
//	println("Cards remaining: ${deck.remaining()}")
//	println("Player 1, which card do you want to play?: ")
//	playerInput = readln().toInt()-1
//	bout.attack(player.hand[playerInput])
//	player.hand.removeAt(playerInput)
//	player.printHand()
//	println(bout.attackDeck)
//	println("Player 2, which card do you want to play?: ")
//	playerInput = readln().toInt()-1
//	bout.defense(player2.hand[playerInput])
//	player2.hand.removeAt(playerInput)
//	player2.printHand()
//	println(bout.defenseDeck)
}