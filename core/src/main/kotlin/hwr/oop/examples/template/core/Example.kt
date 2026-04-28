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
		val drawn_card = deck.draw()
		if (drawn_card != null) {
			hand.add(drawn_card)
		} else {
			println("Drawn card not found")
		}
	}
	fun printHand() {
		for (card in hand) {
			println("$card")
		}
	}
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
	println("Trump card: ${deck.peekTrump()}") // Show trump card
	println("Cards remaining: ${deck.remaining()}")

	repeat(6) {
		player.draw(deck)
		player2.draw(deck)
	}
	println(player.printHand())
	println(player2.printHand())
	println("Cards remaining: ${deck.remaining()}")
	deck.printDeck()
}