package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    private val cards: MutableList<Card>
){
    companion object {
        fun createShuffled(): Deck {
            val cards = mutableListOf<Card>()

            for (suit in Suit.entries) {
                for (rank in Rank.entries) {
                    cards.add(Card(suit, rank))
                }
            }
            cards.shuffle()
            return Deck(cards)
        }
    }

    fun draw(): Card? {
        return if (cards.isNotEmpty()) {
            cards.removeAt(0)
        } else {
            null
        }
    }

    fun remaining(): Int = cards.size

    fun peekTrump(): Card = cards.last()

    fun getCards(): List<Card> = cards.toList()

    fun clearDeckForTest() {
        cards.clear()
    }
    fun addCardToDeckForTest(card: Card) {
        cards.add(card)
    }
}