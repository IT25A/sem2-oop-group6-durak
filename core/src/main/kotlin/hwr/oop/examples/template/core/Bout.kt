package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

/**
 * Stores the cards played by the Attacker/Defender
 */
@Serializable
class Bout {
    val attackDeck: MutableList<Card> = mutableListOf()
    val defenseDeck: MutableList<Card> = mutableListOf()

    /**
     * Adds the provided Card to the attackers' played Cards
     */
    fun attack(card: Card) {
        attackDeck.add(card)
    }

    /**
     * Adds the provided Card to the defenders' played Cards
     */
    fun defense(card: Card) {
        defenseDeck.add(card)
    }
}