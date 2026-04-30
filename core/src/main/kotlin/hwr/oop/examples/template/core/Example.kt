package hwr.oop.examples.template.core

enum class Suit(val label: String) {
	CLUBS("C"), DIAMONDS("D"), HEARTS("H"), SPADES("S")
}

enum class Rank(val rankValue: Int, val label: String) {
	SIX(6, "6"), SEVEN(7, "7"), EIGHT(8, "8"),
	NINE(9, "9"), TEN(10, "10"), JACK(11, "J"),
	QUEEN(12, "Q"), KING(13, "K"), ACE(14, "A")
}
data class Card(val suit: Suit, val rank: Rank){
	 override fun toString(): String {
		return "${rank.label}${suit.label}"
	}
}

class Player(val name: String, val hand: MutableList<Card> = mutableListOf()) {
	fun draw(deck: Deck) {
		val drawnCard = deck.draw()
		if (drawnCard != null) {
			hand.add(drawnCard)
		} else {
			println("Drawn card not found")
		}
	}
	fun printHand() {
		println("---${name}---")
		var i = 1
		for (card in hand) {
			println("[$i] $card")
			i++
		}
		println("------")
	}
}

class Table {
	val attackDeck: MutableList<Card> = mutableListOf()
	val defenceDeck: MutableList<Card> = mutableListOf()
	fun attack(card: Card) {
		attackDeck.add(card)
	}
	fun defence(card: Card) {
		defenceDeck.add(card)
	}
}


class Graveyard {

}

class Deck {
	private val cards: MutableList<Card> = mutableListOf()

	init {
		for (suit in Suit.entries) {
			for (rank in Rank.entries) {
				cards.add(Card(suit, rank))
			}
		}
		shuffle()
	}

	fun shuffle() {
		cards.shuffle()
	}

	fun draw(): Card? {
		if (cards.isNotEmpty()) {
			return cards.removeAt(0)
		}
		return null
	}

	fun printDeck(){
		for (card in cards) {
			println("$card")
		}
	}

	fun isEmpty(): Boolean = cards.isEmpty()

	fun remaining(): Int = cards.size

	fun peekTrump(): Card? = cards.lastOrNull()

	fun getCards(): List<Card> = cards.toList()
}
fun main() {
	val deck = Deck()
	val player = Player("Bob")
	val player2 = Player("Alice")
	val table = Table()
	var playerInput : Int
	println("Trump card: ${deck.peekTrump()}") // Show trump card
	println("Cards remaining: ${deck.remaining()}")

	repeat(6) {
		player.draw(deck)
		player2.draw(deck)
	}
	player.printHand()
	player2.printHand()
	println("Cards remaining: ${deck.remaining()}")
	println("Player 1, which card do you want to play?: ")
	playerInput = readln().toInt()-1
	table.attack(player.hand[playerInput])
	player.hand.removeAt(playerInput)
	player.printHand()
	println(table.attackDeck)
	println("Player 2, which card do you want to play?: ")
	playerInput = readln().toInt()-1
	table.defence(player2.hand[playerInput])
	player2.hand.removeAt(playerInput)
	player2.printHand()
	println(table.defenceDeck)
	//deck.printDeck()
}