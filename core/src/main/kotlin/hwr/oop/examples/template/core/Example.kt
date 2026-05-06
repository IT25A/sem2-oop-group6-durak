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

class Player(val name: String = "", val hand: MutableList<Card> = mutableListOf()) {
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

class Bout {
	val attackDeck: MutableList<Card> = mutableListOf()
	val defenseDeck: MutableList<Card> = mutableListOf()
	fun attack(card: Card) {
		attackDeck.add(card)
	}
	fun defense(card: Card) {
		defenseDeck.add(card)
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

	fun peekTrump(): Card = cards.last()

	fun getCards(): List<Card> = cards.toList()
}

class Game(val playerNames: List<String> = listOf("Bob", "Alice")) {
	val deck = Deck()
	val trump = deck.peekTrump().suit;
	val bout = Bout()
	val players = playerNames.map { name -> Player(name) }
	var attackingPlayer = players[0];
	var defendingPlayer = players[1];

	fun attackPhase(isFirstTurn: Boolean): Boolean {
		var validInput = false

		while (!validInput) {
			attackingPlayer.printHand()
			var input = readln().toInt()-1
			if (input < 1 || input >= attackingPlayer.hand.size-1) {
				println("Invalid Number! (outside of range)")
			}
			else {
				var card = attackingPlayer.hand[input]

				if (isFirstTurn) {
					validInput = true
				}
				else if (bout.attackDeck.any { it.rank == (card.rank) } || bout.defenseDeck.any { it.rank == (card.rank)}) {
					validInput = true
				} else {
					println("${card.rank} has not been played this bout")
				}
			}
		}
		//TODO add card to bout or end turn!
		return true
	}
	fun defendPhase(isFirstTurn: Boolean): Boolean {
		var validInput = false
		val attackCard = bout.attackDeck.last()
		while (!validInput) {
			defendingPlayer.printHand()
			var input = readln().toInt()-1
			if (input < 1 || input >= defendingPlayer.hand.size-1) {
				println("Invalid Number! (outside of range)")
			}
			else {
				var defendCard = defendingPlayer.hand[input]
				if(attackCard.suit != trump && defendCard.suit == trump) {
					validInput = true;
				}
				else if (attackCard.suit == defendCard.suit && defendCard.rank > attackCard.rank){
					validInput = true
				}
				else {
					println("${defendCard.rank} is not a valid card to defend with")
				}
			}
		}
		//TODO add card to bout or end turn!
		return true
	}
	init {
		for (player in players) {
			repeat(6){
				player.draw(deck)
			}
		}
	}
}

fun main() {
	val game = Game()
	var playerInput : Int
	println("Trump card: ${game.deck.peekTrump()}")

	for (player in game.players) {
		player.printHand()
	}
	println("Cards remaining: ${game.deck.remaining()}")
	game.deck.printDeck()

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