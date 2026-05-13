package hwr.oop.examples.template.core

data class Card(val suit: Suit, val rank: Rank){
    override fun toString(): String {
        return "${rank.label}${suit.label}"
    }
}