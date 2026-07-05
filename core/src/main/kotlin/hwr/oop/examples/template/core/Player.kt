package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val name: String,
    private val hand: MutableList<Card>
) {
    companion object {
        fun create(name: String): Player {
            return Player(
                name = name,
                hand = mutableListOf()
            )
        }
    }
    fun getHand() = hand
    fun addAllToHand(list: List<Card>) {
        hand.addAll(list)
    }
    fun addToHand(card: Card) {
        hand.add(card)
    }
    fun removeFromHand(card: Card) {
        hand.remove(card)
    }
    fun clearHand(){
        hand.clear()
    }

    fun draw(deck: Deck) {
        val drawnCard = deck.draw()
        if (drawnCard != null) {
            hand.add(drawnCard)
        } else {
            throw EmptyDeckException("Drawn card not found")
        }
    }
}