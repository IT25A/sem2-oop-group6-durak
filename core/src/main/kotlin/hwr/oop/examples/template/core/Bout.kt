package hwr.oop.examples.template.core

/**
 * Stores the cards played by the Attacker/Defender
 */
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