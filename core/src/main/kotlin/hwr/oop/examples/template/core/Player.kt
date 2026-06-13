package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

@Serializable
class Player(
    val name: String,
    val hand: MutableList<Card>
) {
    companion object {
        fun create(name: String): Player {
            return Player(
                name = name,
                hand = mutableListOf()
            )
        }
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