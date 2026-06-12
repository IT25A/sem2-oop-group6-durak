package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

/**
 * Stores the cards played by the Attacker/Defender
 */
//@Serializable
class Bout {
    val attackDeck: MutableList<Card> = mutableListOf()
    val defenseDeck: MutableList<Card> = mutableListOf()
}