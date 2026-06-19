package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

@Serializable
data class Bout (
    private val attackDeck: MutableList<Card> = mutableListOf(),
    private val defenseDeck: MutableList<Card> = mutableListOf()
) {
    fun getAttackDeck() = attackDeck
    fun getDefenseDeck() = defenseDeck

    fun addAttackCard(card: Card) {
        attackDeck.add(card)
    }
    fun addDefenseCard(card: Card) {
        defenseDeck.add(card)
    }
    fun clearBout() {
        attackDeck.clear()
        defenseDeck.clear()
    }
    fun clearDefenseDeck(){
        defenseDeck.clear()
    }
    fun isBoutDefended(): Boolean {
        return attackDeck.size == defenseDeck.size && attackDeck.isNotEmpty()
    }
}