package hwr.oop.examples.template.core

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

    // fun isEmpty(): Boolean = cards.isEmpty()

    fun remaining(): Int = cards.size

    fun peekTrump(): Card = cards.last()

    fun getCards(): List<Card> = cards.toList()
}