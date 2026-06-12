package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

//@Serializable
class Player(
    val name: String = "",
    val hand: MutableList<Card> = mutableListOf()
) {
    /**
     * The player draws a card from the provided Deck
     * @param deck to draw from
     * @return _True_ if a card was successfully drawn, else returns _false_
     */
    fun draw(deck: Deck) : Boolean {
        val drawnCard = deck.draw()
        if (drawnCard != null) {
            hand.add(drawnCard)
            return true
        } else {
            throw IllegalArgumentException("Drawn card not found")
        }
    }
}