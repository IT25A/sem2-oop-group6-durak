package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

/**
 * Stores the cards played by the Attacker/Defender
 */
@Serializable
data class Bout (
    private val attackDeck: MutableList<Card> = mutableListOf(),
    private val defenseDeck: MutableList<Card> = mutableListOf())
{
    fun attackDeck() = attackDeck
    fun defenseDeck() = defenseDeck
    fun addAttackCard(card: Card) {
        attackDeck.add(card)
    }
    fun addDefenseCard(card: Card) {
        defenseDeck.add(card)
    }
    fun clear() {
        attackDeck.clear()
        defenseDeck.clear()
    }
    fun clearAttackDeck(){
        attackDeck.clear()
    }
    fun clearDefenseDeck(){
        defenseDeck.clear()
    }
    fun isBoutDefended(): Boolean {
        return attackDeck.size == defenseDeck.size && attackDeck.isNotEmpty()
    }
}