package hwr.oop.examples.template.core

/**
 * @param suit [Suit]
 * @param rank [Rank]
 */
data class Card(
    val suit: Suit,
    val rank: Rank
){
    fun suit(): Suit = suit
    fun rank(): Rank = rank
}