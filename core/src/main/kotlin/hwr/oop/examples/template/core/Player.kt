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
    fun hand() = hand

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

    fun draw(deck: Deck): Boolean {
        val drawnCard = deck.draw()
        if (drawnCard != null) {
            hand.add(drawnCard)
            return true
        } else {
            throw IllegalArgumentException("Drawn card not found")
        }
    }
}