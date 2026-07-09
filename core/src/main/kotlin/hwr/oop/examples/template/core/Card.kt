package hwr.oop.examples.template.core

import kotlinx.serialization.Serializable

/**
 * @param suit [Suit]
 * @param rank [Rank]
 */
@Serializable
data class Card(
    val suit: Suit,
    val rank: Rank
) {
    fun toShortString(): String {
        val rankStr = when (rank) {
            Rank.SIX   -> "6"
            Rank.SEVEN -> "7"
            Rank.EIGHT -> "8"
            Rank.NINE  -> "9"
            Rank.TEN   -> "10"
            Rank.JACK  -> "J"
            Rank.QUEEN -> "Q"
            Rank.KING  -> "K"
            Rank.ACE   -> "A"
        }
        val suitStr = when (suit) {
            Suit.CLUBS    -> "C"
            Suit.DIAMONDS -> "D"
            Suit.HEARTS   -> "H"
            Suit.SPADES   -> "S"
        }
        return "$rankStr$suitStr"
    }
}