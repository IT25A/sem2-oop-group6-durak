package hwr.oop.examples.template.core

/**
 * @constructor Creates a deck containing 36 unique cards **pre-shuffled**
 */
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

    /**
     * _Removes_ the top card of the deck and returns it
     * @return the **top card** or _null if deck is empty_
     */
    fun draw(): Card? {
        if (cards.isNotEmpty()) {
            return cards.removeAt(0)
        }
        return null
    }

    fun remaining(): Int = cards.size

    /**
     * @return the bottom card of the deck
     */
    fun peekTrump(): Card = cards.last()

    /**
     * @return all cards as a List<Card>
     */
    fun getCards(): List<Card> = cards.toList()

    fun clearDeckForTest() {
        cards.clear()
    }
    fun addCardToDeckForTest(card: Card) {
        cards.add(card)
    }
}