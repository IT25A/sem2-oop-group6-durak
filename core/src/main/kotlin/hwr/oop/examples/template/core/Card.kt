package hwr.oop.examples.template.core

/**
 * @param suit [Suit]
 * @param rank [Rank]
 */
data class Card(val suit: Suit, val rank: Rank){
    /**
     * @return [rank] and [suit] **label** as a string
     */
    override fun toString(): String {
        return "${rank.label}${suit.label}"
    }
}